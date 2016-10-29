package lense.compiler.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import lense.compiler.asm.ByteCodeTypeDefinitionReader;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.TypeSearchParameters;

public class ClasspathRepository implements TypeRepository{

	List<ModuleRepository> modules = new ArrayList<>();
	Map<TypeSearchParameters, TypeDefinition> types = new HashMap<>();
	private File base;

	public File getBase(){
		return base;
	}
	
	public ClasspathRepository(File path){
		this.base = path;
		
		ByteCodeTypeDefinitionReader reader = new ByteCodeTypeDefinitionReader();
		try {
			for (File jarFile : path.listFiles(f -> f.isFile() && f.getName().endsWith(".jar"))){
				ModuleRepository moduleRepo = new ModuleRepository("", Version.valueOf("0.0.0"));

				modules.add(moduleRepo);
				java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile);

				Manifest manifest = jar.getManifest();


				java.util.Enumeration enumEntries = jar.entries();
				while (enumEntries.hasMoreElements()) {
					java.util.jar.JarEntry file = (java.util.jar.JarEntry) enumEntries.nextElement();

					if (file.getName().endsWith(".class")){
						try (java.io.InputStream is = jar.getInputStream(file)){
							TypeDefinition type = reader.readNative(is);
							moduleRepo.registerType(type, type.getGenericParameters().size());
						}
					} else if (file.getName().equals("module.properties")){
						try (java.io.InputStream is = jar.getInputStream(file)){
							Properties pp = new Properties();
							pp.load(is);
							moduleRepo.setVersion( Version.valueOf(pp.getProperty("module.version")));
							moduleRepo.setName( pp.getProperty("module.name"));
						}
					}
				}
				
				moduleRepo.simplify();
			}
		} catch (IOException e){
			// TODO static constructor
		}
	}

	@Override
	public Optional<TypeDefinition> resolveType(TypeSearchParameters filter) {
		TypeDefinition type = types.get(filter);
		
		if (type == null){
			for(ModuleRepository module : modules){
				Optional<TypeDefinition> opType = module.resolveType(filter);
				
				if (opType.isPresent()){
					types.put(filter, opType.get());
					return opType;
				}
			}
			return Optional.empty();
		}
		
		return Optional.of(type);
	}

	@Override
	public List<ModuleRepository> resolveModuleByName(QualifiedNameNode qualifiedNameNode) {
		return modules.stream().filter(m -> m.getName().equals(qualifiedNameNode.getName())).collect(Collectors.toList());
	}

	@Override
	public Optional<ModuleRepository> resolveModuleByNameAndVersion(QualifiedNameNode qualifiedNameNode, Version version) {
		return modules.stream().filter(m -> m.getName().equals(qualifiedNameNode.getName()) && m.getVersion().equals(version)).findAny();
	}



}
