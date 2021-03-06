package lense.compiler.repository;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import compiler.filesystem.DiskSourceFileSystem;
import compiler.filesystem.SourceFile;
import compiler.filesystem.SourceFileSystemException;
import lense.compiler.asm.ByteCodeTypeDefinitionReader;
import lense.compiler.asm.ProxyTypeDefinition;
import lense.compiler.asm.TypeDefinitionInfo;
import lense.compiler.dependency.DependencyGraph;
import lense.compiler.dependency.DependencyRelation;
import lense.compiler.dependency.DependencyRelationship;
import lense.compiler.graph.FollowTransversor;
import lense.compiler.graph.FollowVertexTraversalEvent;
import lense.compiler.graph.GraphTranverseListener;
import lense.compiler.graph.VertexTraversalEvent;
import lense.compiler.modules.EditableModuleDescriptor;
import lense.compiler.modules.ModuleTypeContents;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.TypeSearchParameters;
import lense.compiler.utils.Strings;

public class JarTypeRepository extends AbstractTypeRepositoryWithDependencies {

	private final SourceFile moduleFile;

	private ModuleTypeContents moduleRepo = null;

	public JarTypeRepository(SourceFile jarFile) {
		this.moduleFile = jarFile;
	}

	@Override
	public Optional<TypeDefinition> resolveType(TypeSearchParameters filter) {

		if(moduleRepo == null) {
			loadTypes();
		}

		return moduleRepo.resolveType(filter);

	}

	@Override
	public Map<Integer, TypeDefinition> resolveTypesMap(String name) {

		if(moduleRepo == null) {
			loadTypes();
		}

		return moduleRepo.resolveTypesMap(name);
	}



	private void loadTypes() {

		var descriptor = new EditableModuleDescriptor();

		this.moduleRepo = new ModuleTypeContents(descriptor);

		ByteCodeTypeDefinitionReader reader = new ByteCodeTypeDefinitionReader(new UpdatableTypeRepository() {

			@Override
			public Optional<TypeDefinition> resolveType(TypeSearchParameters filter) {
				var type = moduleRepo.resolveType(filter);

				if (type.isEmpty()) {
					type = resolveTypeFromDependencies(filter);
				}

				return type;
			}

			@Override
			public Map<Integer, TypeDefinition> resolveTypesMap(String name) {
				return moduleRepo.resolveTypesMap(name);
			}

			@Override
			public TypeDefinition registerType(TypeDefinition type, int genericParametersCount) {
				return moduleRepo.registerType(type,genericParametersCount);
			}
		});

		var graph = new DependencyGraph<TypeDefinitionInfo>();

		var set = new HashSet<String>();

		var module = new TypeDefinitionInfo("__module__", LenseUnitKind.Module);

		try(java.util.jar.JarFile jar = new java.util.jar.JarFile(DiskSourceFileSystem.instance().convertToFile(moduleFile))){

			java.util.Enumeration<java.util.jar.JarEntry> enumEntries = jar.entries();
			while (enumEntries.hasMoreElements()) {
				java.util.jar.JarEntry file =  enumEntries.nextElement();

				if (file.getName().endsWith(".class")){
					try (java.io.InputStream is = jar.getInputStream(file)){


						var info = reader.readInfo(is);
						set.remove(info.name);

						Optional<TypeDefinitionInfo> it = graph.findDependencyNode(info.name);

						if(it.isPresent()) {

							var copy = it.get();

							copy.builder = info.builder;
							copy.genericCount = info.genericCount;
							copy.imports = info.imports;
							copy.kind = info.kind;


							for(var other : info.imports) {
								TypeDefinitionInfo od = graph.findDependencyNode(other.name).orElseGet(() -> other);
								set.add(other.name);

								System.out.println("#1 type " + copy.name + " dependends on " + od.name + "(" + od.kind + ")");

								if(other.kind != LenseUnitKind.Interface) {
									graph.addEdge(new DependencyRelation(DependencyRelationship.Structural),od,copy);

								}
							}



						} else {
							//graph.addEdge(new DependencyRelation(DependencyRelationship.Module),info,module);
							if (info.imports.isEmpty()) {
								graph.addEdge(new DependencyRelation(DependencyRelationship.Module),info,module);
							} else {

								for(var other : info.imports) {
									TypeDefinitionInfo od = graph.findDependencyNode(other.name).orElseGet(() -> other);
									set.add(other.name);

									System.out.println("#2 type " + info.name + " dependends on " + od.name + "(" + od.kind + ")");
									if(other.kind != LenseUnitKind.Interface) {
										graph.addEdge(new DependencyRelation(DependencyRelationship.Structural),od,info);
										//graph.addEdge(new DependencyRelation(DependencyRelationship.Module),od,module);
									}
								}
							}
						}






					}
				} else if (file.getName().equals("module.properties")){
					try (java.io.InputStream is = jar.getInputStream(file)){
						Properties pp = new Properties();
						pp.load(is);

						var version = Version.valueOf(pp.getProperty("module.version"));
						var name= pp.getProperty("module.name");

						var dependencies = pp.getProperty("module.requires");

						if(dependencies != null) {
							for (var dep : Strings.split(dependencies,";")){
								var req = Strings.split(dep,"@");
								descriptor.addRequiredModule(new EditableModuleDescriptor(req[0], Version.valueOf(req[1])));
							}

						}

						descriptor.setVersion(version );
						descriptor.setName( name);

					}
				}
			}

			//            trasnverse

			var infoTransversor = new FollowTransversor<DependencyRelation,TypeDefinitionInfo>();

			infoTransversor.addListener(new GraphTranverseListener<>() {

				@Override
				public void beginVertex(VertexTraversalEvent<TypeDefinitionInfo, DependencyRelation> e) {

					var f = (FollowVertexTraversalEvent)e;

					var info = e.getVertex().getObject();

					System.out.print("# visiting " + info.name);
					System.out.println(f.isFirstcross() ? " concrete" : " proxy" );

					if (f.isFirstcross()){
						if(info.builder != null) {
							var map= moduleRepo.resolveTypesMap(info.name);

							ProxyTypeDefinition proxy = null;
							if (!map.isEmpty()) {
								var existing = map.values().iterator().next();
								if (existing instanceof ProxyTypeDefinition) {
									proxy = (ProxyTypeDefinition)existing;
									moduleRepo.removeType(info.name);
								} else {
									System.out.println("# same " + info.name);
								}
							}

							var type = info.builder.build();

							if(proxy != null) {
								proxy.setOriginal(type);
							}
							System.out.println("# loading " + info.name);

							if (!type.isPlataformSpecific()){
								moduleRepo.registerType(type, type.getGenericParameters().size());
							}

							set.remove(type.getName());
						}
					} else {
						if (moduleRepo.resolveTypesMap(info.name).isEmpty()) {
							var proxy = new ProxyTypeDefinition(info.name);
							moduleRepo.registerType(proxy, 0);
						}
					}

				}

			});

			if(descriptor.getName().equals("lense.core")) {
				//				add non denotable types
				moduleRepo.registerType(LenseTypeSystem.Void(),0);
				moduleRepo.registerType(LenseTypeSystem.Nothing(),0);
				moduleRepo.registerType(LenseTypeSystem.Any(),0);
			}

			infoTransversor.transverse(graph, null);

			moduleRepo.getDescription();
		} catch (IOException e) {
			throw new SourceFileSystemException(e);
		}

	}



}
