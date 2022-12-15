package lense.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import compiler.AstCompiler;
import compiler.CompilationResult;
import compiler.CompilationResultSet;
import compiler.CompilationUnit;
import compiler.CompilationUnitSet;
import compiler.CompiledUnit;
import compiler.CompilerBackEnd;
import compiler.CompilerListener;
import compiler.CompilerMessage;
import compiler.ComposedCompilerBackEnd;
import compiler.ListCompilationUnitSet;
import compiler.ReaderCompilationUnit;
import compiler.SourceFolderCompilationUnitSet;
import compiler.StringCompilationUnit;
import compiler.filesystem.DiskSourceFileSystem;
import compiler.filesystem.SourceFile;
import compiler.filesystem.SourceFolder;
import compiler.trees.TreeTransverser;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ModuleNode;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.UnitTypes;
import lense.compiler.dependency.CompilationUnitDependencyNode;
import lense.compiler.dependency.CyclicDependencyResolver;
import lense.compiler.dependency.DependencyGraph;
import lense.compiler.dependency.DependencyRelation;
import lense.compiler.dependency.DependencyRelationship;
import lense.compiler.dependency.ModuleDependencyGraph;
import lense.compiler.dependency.ModuleDependencyNode;
import lense.compiler.graph.Graph.Vertex;
import lense.compiler.graph.GraphTransversor;
import lense.compiler.graph.GraphTranverseListener;
import lense.compiler.graph.TopologicOrderTransversor;
import lense.compiler.graph.VertexTraversalEvent;
import lense.compiler.modules.EditableModuleDescriptor;
import lense.compiler.modules.ModuleDescription;
import lense.compiler.modules.ModuleUnit;
import lense.compiler.modules.ModulesRepository;
import lense.compiler.phases.CompositePhase;
import lense.compiler.phases.ConstructorDesugarPhase;
import lense.compiler.phases.NameResolutionPhase;
import lense.compiler.phases.OtimizationPhase;
import lense.compiler.phases.SemanticAnalysisPhase;
import lense.compiler.phases.TypeClassInterpolationPhase;
import lense.compiler.repository.ModuleCompilationScopeTypeRepository;
import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.TypeSearchParameters;

public abstract class LenseCompiler {

	private CompilerListener listener = new CompilerListener(){

		@Override
		public void start() {

		}

		@Override
		public void error(CompilerMessage error) {
			System.err.println(error.getMessage());
		}

		@Override
		public void warn(CompilerMessage error) {

		}

		@Override
		public void end() {

		}

		@Override
		public void trace(CompilerMessage trace) {

		}

	};

	private ModulesRepository globalRepository;
	private String nativeLanguageName;

	private CompilerBackEndFactory backendFactory;

	protected DependencyGraph<CompilationUnitDependencyNode> graph;

	protected boolean selfCompilation;

	public LenseCompiler (String nativeLanguageName, ModulesRepository globalRepository,CompilerBackEndFactory backendFactory){
		// The global, even remote, repository
		this.globalRepository = globalRepository;
		this.nativeLanguageName = nativeLanguageName;
		this.backendFactory = backendFactory;
		backendFactory.setClasspath(globalRepository.getClassPath());
	}

	public LenseCompiler setCompilerListener(CompilerListener listener){
		this.listener = listener;
		return this;
	}

	protected CompilerListener getCompilerListener(){
		return this.listener;
	}

	private FileLocations defineFileLocations(SourceFolder moduleprojectFolder) throws IOException{

		var sources = moduleprojectFolder.folder("source"); 

		if (!sources.exists()){
			listener.error(new CompilerMessage("No sources found. No folder " + sources.toString() + " exists"));
		}

		var modulesOut = moduleprojectFolder.folder("compilation").folder(nativeLanguageName).folder("bin");  
		if (!modulesOut.exists()){
			modulesOut.ensureExists();
		}

		var nativeSources = moduleprojectFolder.folder("native").folder(nativeLanguageName);


		var target = moduleprojectFolder.folder("compilation").folder(nativeLanguageName).folder("target"); 


		// delete previous run
		if (!target.exists()){
			target.ensureExists();
		} else {
			target.delete();
		}

		var modules = moduleprojectFolder.folder("compilation").folder("modules"); 

		return new FileLocations(target, nativeSources, sources, modules);
	}

	protected abstract void createModuleArchive(FileLocations locations, ModuleNode module, Set<String> applications) throws IOException, FileNotFoundException;
	protected abstract void initCorePhase(CompositePhase corePhase, Map<String, SourceFile> nativeTypes, UpdatableTypeRepository typeContainer);
	protected abstract void collectNative(FileLocations fileLocations, Map<String, SourceFile> nativeTypes) throws IOException;
	protected abstract SourceFile resolveNativeFile(SourceFolder folder, String name);

	public void compileUnit(Reader reader)  {

		compileUnit(new ReaderCompilationUnit(reader));
	}

	public void compileUnit(String code) {
		compileUnit(new StringCompilationUnit(code));
	}

	public void compileUnit(CompilationUnit unit)  {

		ListCompilationUnitSet set = new ListCompilationUnitSet();
		set.add(unit);

		var rootfolder = DiskSourceFileSystem.instance().folder(new File("."));

		var temp = rootfolder.folder("temp");
		temp.ensureExists();

		var target = temp.folder("target");

		target.ensureExists();

		FileLocations locations = new  FileLocations(target,temp,temp,temp);

		ListCompilationUnitSet module = new ListCompilationUnitSet();
		module.add(new StringCompilationUnit("module anonymous (0.0.0) { import lense.core 0.1.0; }"));

		compileCompilationUnitSet(module, set, locations);


	}

	public void compileModuleFromDirectory(SourceFolder moduleproject){

		try {
			FileLocations locations = this.defineFileLocations(moduleproject);

			CompilationUnitSet moduleUnit = new SourceFolderCompilationUnitSet(locations.getSourceFolder() , fileName -> fileName.equals("module.lense"));

			CompilationUnitSet unitSet = new SourceFolderCompilationUnitSet(locations.getSourceFolder() , name -> !name.equals("module.lense") && name.endsWith(".lense"));

			compileCompilationUnitSet(moduleUnit, unitSet, locations);
		} catch (Exception e) {
			listener.error(new CompilerMessage(e.getMessage()));
		} finally {
			listener.end();
		}
	}

	protected abstract List<TypeDefinition> extactTypeDefinitionFromNativeType(UpdatableTypeRepository currentTypeRepository , Collection<SourceFile> files) throws IOException;

	public void compileCompilationUnitSet(CompilationUnitSet moduleUnit,CompilationUnitSet unitSet, FileLocations locations){


		Map<String, SourceFile> nativeTypes = new HashMap<>();

		listener.start();
		try {

			AstCompiler parser = new AstCompiler(new LenseLanguage());

			// compile lense files

			// first, compile the module information

			final List<CompiledUnit> modulesList = parser.parse(moduleUnit).sendToList();

			if (modulesList.isEmpty()){
				listener.error(new CompilerMessage("Could not find module.lense file in the source."));
			}

			// locate module definition
			ModuleNode module  = (ModuleNode) modulesList.get(0).getAstRootNode().getChildren().get(0);

			EditableModuleDescriptor moduleDescriptor = new EditableModuleDescriptor();

			TreeTransverser.transverse(module, new ModuleDescriptionReaderVisitor(moduleDescriptor));

			this.selfCompilation = moduleDescriptor.getName().equals("lense.core");

			ModuleDependencyGraph moduleGraph = new ModuleDependencyGraph();

			if (!selfCompilation){ // if not compiling lense core

				// create module graph

				ModuleDependencyNode root = new ModuleDependencyNode(moduleDescriptor);

				populateModuleGraph(moduleGraph, root);


				Optional<ModuleDescription> coreModule = moduleDescriptor.getRequiredModuleByName("lense.core");

				if (coreModule.isPresent()){
					// TODO match the current version for compatibly and/or take the SDK for that version
				} else {
					// assume the current version is the target
				}
			}

			// Define the repository being mounted
			ModuleCompilationScopeTypeRepository currentModuleRepository =  new ModuleCompilationScopeTypeRepository(moduleDescriptor);


			GraphTransversor<DependencyRelation, ModuleDependencyNode> moduleTransversor = new TopologicOrderTransversor<>();


			var map = new HashMap<ModuleDescription,ModuleUnit>();

			moduleTransversor.addListener(new GraphTranverseListener<>() {


				@Override
				public void beginVertex(VertexTraversalEvent<ModuleDependencyNode, DependencyRelation> e) {

					var requiredModule = e.getVertex().getObject().getModuleDescription();

					if(requiredModule.equals(moduleDescriptor)) {
						for(var dep : e.getVertex().getIncidentEdges()) {
							var depcontent = map.get(dep.getSourceVertex().getObject().getModuleDescription());

							if(depcontent != null) {
								currentModuleRepository.addRequiredModule(depcontent);
							}

						}
					} else {

						ModuleUnit module = globalRepository.resolveModuleByNameAndVersion(requiredModule.getModuleIdentifier()).orElseThrow(
								() -> new CompilationError("Cannot import module " + requiredModule.getName() + " version " + requiredModule.getVersion() + ". Is it in the local repository?")
						);

						for(var dep : e.getVertex().getIncidentEdges()) {
							var depcontent = map.get(dep.getSourceVertex().getObject().getModuleDescription());

							if(depcontent != null) {
								module.getTypeRepository().addDependency(depcontent.getTypeRepository());
							}

						}

						map.put(requiredModule,module);
					}
				}


			});

			moduleGraph.findDependencyNode(moduleDescriptor.getName()).ifPresent( m ->  moduleTransversor.transverse(moduleGraph, m));
		

			// first compile target language native files

			collectNative(locations, nativeTypes);

			CompositePhase corePhase = new CompositePhase()
					.add(new SemanticAnalysisPhase(currentModuleRepository, listener))
					.add(new OtimizationPhase(listener));

			initCorePhase (corePhase, nativeTypes, currentModuleRepository);

			//      		ByteCodeTypeDefinitionReader reader = new ByteCodeTypeDefinitionReader(currentModuleRepository);

			if (selfCompilation) {
				// non denotable
				currentModuleRepository.registerType(LenseTypeSystem.Any(), 0);
				currentModuleRepository.registerType(LenseTypeSystem.Nothing(), 0);
				currentModuleRepository.registerType(LenseTypeSystem.Void(), 0);
				currentModuleRepository.registerType(LenseTypeSystem.None(), 0);
			}

	
			
			// the root node is the module itself
			CompilationUnitDependencyNode moduleNode = new CompilationUnitDependencyNode(modulesList.get(0), module.getName());

			// load source files

			Map<QualifiedNameNode, List<ClassTypeNode>> packagesMapping = new HashMap<>(); 
			Set<String> foundNames = new HashSet<>();
			Set<String> referencedNames = new HashSet<>();
			Set<String> applications = new HashSet<>();

			// init found names with nothing because it is a non denotable
			foundNames.add("lense.core.lang.Nothing");

			var nameResolutionPhase = (new NameResolutionPhase(currentModuleRepository, new PathPackageResolver(locations.getSourceFolder().getPath()), listener));
			
			CompositePhase prePhase = new CompositePhase()
					.add(new ConstructorDesugarPhase(listener)) 
					.add(nameResolutionPhase);

			trace("Creating dependency graph");


			String ref = "lense.core.lang.reflection.ReifiedArguments";

			// create dependency graph
			this.graph = new DependencyGraph<CompilationUnitDependencyNode>();

			parser.parse(unitSet)
			.passBy(prePhase)
			.peek(compilationUnit -> {

				trace("Analysing unit file " + compilationUnit);

				UnitTypes t = (UnitTypes) compilationUnit.getAstRootNode();

				for(ClassTypeNode type : t.getTypes()){

					trace("Analysing type " + type.getFullname());

					foundNames.add(type.getFullname());

					// the module depends on every type inside

					// check if the type already exists in the graph
					Optional<CompilationUnitDependencyNode> it = graph.findDependencyNode(type.getFullname());
					CompilationUnitDependencyNode dependency;

					if (it.isPresent()){
						dependency = it.get();
						dependency.setUnit(compilationUnit);

						if (type.getFullname().equals(ref)) {
							dependency.setUnit(compilationUnit);
						}
					} else {
						dependency = new CompilationUnitDependencyNode(compilationUnit, type.getFullname());
					}

					graph.addEdge(new DependencyRelation(DependencyRelationship.Module), dependency, moduleNode);


					if(type.getSemanticContext().getCurrentPackageName() != null && type.getSemanticContext().getCurrentPackageName().length() > 0) {
						if(type.getKind() != LenseUnitKind.Interface) {
							 var list = packagesMapping.computeIfAbsent(new QualifiedNameNode(type.getSemanticContext().getCurrentPackageName()), (k) -> new ArrayList<>());
							 list.add(type);
						}
					}


					for(Import imp : type.imports()){
						String name = imp.getTypeName().getName();
						it = graph.findDependencyNode(name);
						CompilationUnitDependencyNode imported;
						if (it.isPresent()){
							imported = it.get();
						}else {
							imported = new CompilationUnitDependencyNode(null, name);
						}

						if (shouldGraphContain(DependencyRelationship.Structural) && (imp.isMemberCalled() || imp.isSuper())){
							trace(dependency.getName() + " strongly depends on " + imported.getName());
							if (imported.getName().equals("lense.core.system.ConsoleApplication")){
								applications.add(dependency.getName());
							}
							graph.addEdge(new DependencyRelation(DependencyRelationship.Structural),  imported, dependency);
						} else if (shouldGraphContain(DependencyRelationship.Parameter) && imp.isMemberSignatureElement()) {
							if (imp.isNativeUse()) {
								trace(dependency.getName() + " has " + imported.getName() + " as native parameter");
							
							} else {
								trace(dependency.getName() + " has " + imported.getName() + " as parameter");
								
								graph.addEdge(new DependencyRelation(DependencyRelationship.Parameter),  imported, dependency);
							}

						} else if (dependency.getName().equals(imported.getName())){
							trace(dependency.getName() + " referes by to it self ");
							continue;
						} else {
							trace(dependency.getName() + " declared import of " + imported.getName());
						}

						referencedNames.add(imported.getName());

					}
					

				}



			}).sendToList();


			referencedNames.removeAll(foundNames);
			
			var typeDefs = this.extactTypeDefinitionFromNativeType(currentModuleRepository, nativeTypes.values());

			typeDefs.forEach(type -> currentModuleRepository.registerType(type, type.getGenericParameters().size()));


			var cycle = new CyclicDependencyResolver().resolveIncidentCycle(graph);

			if (cycle.isPresent()) {
				var path = cycle.get().stream().map(v -> v.getObject().getName()).collect(Collectors.joining("->"));

				throw new CompilationError("Cycle found: " + path);
			}


			for(Iterator<String> it = referencedNames.iterator(); it.hasNext(); ){
				if (currentModuleRepository.resolveType(new TypeSearchParameters(it.next())).isPresent()){
					it.remove();
				}
			}

			if (!referencedNames.isEmpty()){
				throw new CompilationError( "Type " + referencedNames.stream().filter(r -> r.length() > 0).findFirst().get() + " was referenced, but not found");
			}

			trace("Compiling graph");


			final CompilerBackEnd backend = new ComposedCompilerBackEnd().add(backendFactory.create(locations))
					//            		.add(new DefinitionsBackEnd(locations))
					;

			GraphTransversor<DependencyRelation, CompilationUnitDependencyNode> tt = new TopologicOrderTransversor<>();

			tt.addListener(new GraphTranverseListener<CompilationUnitDependencyNode, DependencyRelation>() {

				@Override
				public void beginVertex(VertexTraversalEvent<CompilationUnitDependencyNode, DependencyRelation> e) {
					trace("Visiting : " + e.getVertex().getObject().getName());
					CompiledUnit unit = e.getVertex().getObject().getCompiledUnit();

					if (unit != null && !unit.getProperty("visited", Boolean.class).orElse(false)) {

						applyCompilation(nativeTypes, locations, corePhase, currentModuleRepository, backend, unit);
						unit.setProperty("visited", Boolean.TRUE);
					} 


					trace("Visited : " + e.getVertex().getObject().getName());
				}
			});

			// apply any first
			Optional<Vertex<CompilationUnitDependencyNode, DependencyRelation>> any = graph.getVertices().stream().filter(v -> v.getObject().getName().equals(LenseTypeSystem.Any().getName())).findAny();

			if (selfCompilation && any.isPresent()) {
				applyCompilation(nativeTypes, locations, corePhase, currentModuleRepository, backend, any.map(v -> v.getObject().getCompiledUnit()).get());

				graph.removeVertex(any.get());
			}


			tt.transverse(graph, moduleNode);

			
			// produce type classes
			ListCompilationUnitSet all = new ListCompilationUnitSet();
			for(var entry : packagesMapping.entrySet()){

				for (var type : entry.getValue()) {
					if (type.getTypeDefinition() != null) {
						StringBuilder builder = writeType( type);
						var typeName = type.getSimpleName() + "$$Type.lense";
						var path = locations.getSourceFolder().folder(PackageSourcePathUtils.fromPackageName(entry.getKey().getName())).file(typeName).getPath();

						all.add(new StringCompilationUnit(builder.toString(), path));	
					}
				}
			}

			parser.parse(all)
			.passBy(nameResolutionPhase)
			.passBy(corePhase)
			.passBy(new TypeClassInterpolationPhase(packagesMapping.values().stream().flatMap(it -> it.stream()).toList()))
			.sendTo(backend);
			
			// produce package classes
		    all = new ListCompilationUnitSet();
			for(var entry : packagesMapping.entrySet()){

				StringBuilder builder = writePackage(entry.getKey(), entry.getValue());

				var path = locations.getSourceFolder().folder(PackageSourcePathUtils.fromPackageName(entry.getKey().getName())).file("Package$$Info.lense").getPath();

				all.add(new StringCompilationUnit(builder.toString(), path));
			}

			parser.parse(all)
			.passBy(nameResolutionPhase)
			.passBy(corePhase)
			.sendTo(backend);

			// produce module metadata and class

			StringBuilder builder = writeModule(module, packagesMapping.keySet());
			all = new ListCompilationUnitSet();
			all.add(new StringCompilationUnit(builder.toString(), locations.getSourceFolder().folder(PackageSourcePathUtils.fromPackageName(module.getName())).file("Module$$Info.lense").getPath())); // TODO specify package

			parser.parse(all)
			.passBy(nameResolutionPhase)
			.passBy(corePhase)
			.sendTo(backend);

			var modules = locations.getModulesFolder();
			if (!modules.exists()){
				modules.ensureExists();
			}

			createModuleArchive(locations, module, applications);

		} catch (Exception e) {
			e.printStackTrace();
			listener.error(new CompilerMessage(e.getMessage()));
		} finally {
			listener.end();
		}

	}



	protected abstract boolean shouldGraphContain(DependencyRelationship parameter);

	private void populateModuleGraph(ModuleDependencyGraph moduleGraph, ModuleDependencyNode root) {


		var deque = new LinkedList<ModuleDependencyNode>();

		for (var m : root.getModuleDescription().getRequiredModules()) {

			var pnode = moduleGraph.findDependencyNode(m.getName());
			ModuleDependencyNode node;
			if(!pnode.isPresent()) {
				node = new ModuleDependencyNode(m);
				deque.add(node);
			} else {
				node = pnode.get();
			}

			moduleGraph.addEdge(new DependencyRelation(DependencyRelationship.Module), node , root);

		}

		for (var n : deque) {

			var description =  globalRepository.resolveModuleByNameAndVersion(n.getModuleDescription().getIdentifier())
					.map(s -> s.getModuleDescription())
					.orElseThrow(() -> new CompilationError("Required module " + n.getModuleDescription().getName() + " was not found"));

			n.setModuleDescription(description);

			populateModuleGraph(moduleGraph, n);
		}

	}

	protected void applyCompilation(
			Map<String, SourceFile> nativeTypes, 
			FileLocations locations,
			CompositePhase corePhase,
			ModuleCompilationScopeTypeRepository currentModuleRepository, 
			final CompilerBackEnd backend,
			CompiledUnit unit
	) {
		if (unit != null){
			new CompilationResultSet(new CompilationResult(unit)).passBy(corePhase).sendTo(backend);
		}
	}

	private void trace(String message){
		this.listener.trace(new CompilerMessage(message));
	}

	private StringBuilder writeModule(ModuleNode module, Set<QualifiedNameNode> packages) {
		StringBuilder builder = new StringBuilder("import lense.core.lang.reflection.Module;\n")
				.append(" import lense.core.lang.String;\n import lense.core.lang.Boolean;\n")
				.append(" import lense.core.lang.Version;\n import lense.core.collections.Sequence; import lense.core.collections.LinkedList;\n")
				.append(" import lense.core.lang.reflection.Package;\n import lense.core.lang.Any;\n");

		int i=1;
		for(QualifiedNameNode pack : packages){ 
			builder.append(" import ").append(pack).append(".Package$$Info as Pack").append(i).append(";\n");
			i++;
		}

		builder.append("public class ").append("Module$$Info implements Module {\n");

		builder.append("public getName() : String{\n");
		builder.append("    return \"").append(module.getName()).append("\";\n");
		builder.append("}\n");

		builder.append("public getVersion(): Version{\n");
		builder.append("    return new Version(\"").append(module.getVersion()).append("\");\n");
		builder.append("}");
		builder.append("public getPackages() : Sequence<Package> {\n");
		builder.append("    let  all : LinkedList<Package>= new LinkedList<Package>();\n ");

		for(i =0; i < packages.size(); i++){ 
			builder.append("    all.add(new Pack").append(i+1).append("());\n");
		}

		builder.append("    return all;");
		builder.append("}\n")
		.append(" public override equalsTo( other : Any) : Boolean {\n")
		.append("   return false;\n")
		.append("}\n")
		.append(" public override hashValue() : HashValue {\n")
		.append("   return getName().hashValue();\n")
		.append("}\n");
		builder.append("}");
		return builder;
	}

	private StringBuilder writePackage(QualifiedNameNode pack, List<ClassTypeNode> types) {
		StringBuilder builder = new StringBuilder("import lense.core.lang.reflection.Package; import lense.core.lang.reflection.Type; import lense.core.lang.Any; import lense.core.lang.String; import lense.core.collections.LinkedList; import lense.core.lang.Boolean;");
				for(int i =0; i < types.size(); i++){ 
					builder.append("import ").append(types.get(i).getPackageName())
					.append(".")
					.append(lense.compiler.utils.Strings.cammelToPascalCase(types.get(i).getSimpleName()))
					.append("$$Type;\n");
				}
				
				builder.append("public class Package$$Info implements Package { \n")
				.append(" public constructor ();")
				.append(" public getName() : String {\n")
				.append("   return \"").append(pack).append("\" ;\n")
				.append("}\n")
				.append("public types() : Sequence<Type> {\n")
				.append("    let  all = new LinkedList<Type>();\n ");
		
				for(int i =0; i < types.size(); i++){ 
					builder.append("    all.add(new ")
					.append(lense.compiler.utils.Strings.cammelToPascalCase(types.get(i).getSimpleName()))
					.append("$$Type());\n");
				}
		
				builder.append("    return all;")
				.append("}\n")
				.append(" public override equalsTo( other: Any) : Boolean {\n")
				.append("   return false;\n")
				.append("}\n")
				.append(" public override hashValue() : HashValue {\n")
				.append("   return getName().hashValue();\n")
				.append("}\n")
				.append("}\n");
				
		return builder;
	}
	

	private StringBuilder writeType(ClassTypeNode node) {
		
		node.setProperty("typeClassMode", true);
		var type = node.getTypeDefinition();
		
		var name = lense.compiler.utils.Strings.cammelToPascalCase(type.getSimpleName()) + "$$Type";
		
		StringBuilder builder = new StringBuilder("import lense.core.lang.reflection.Type; import lense.core.lang.reflection.Method; import lense.core.lang.reflection.Property;  import lense.core.collections.LinkedList; import lense.core.lang.reflection.ReflectiveMethod; import lense.core.lang.reflection.ReflectiveProperty;\n");
				
				if (node.getSatisfiedTypeClasses() != null) {
					for (var typeClass : node.getSatisfiedTypeClasses().getChildren(TypeNode.class)) {
						builder.append("import ").append(typeClass.getTypeParameter().getTypeDefinition().getName()).append(";\n");
					}
				}
			
				
				builder.append("public class ").append(name).append(" extends Type ");
				
				if (node.getSatisfiedTypeClasses() != null) {
					builder.append("  implements ");
					var iterator = node.getSatisfiedTypeClasses().getChildren(TypeNode.class).iterator();
					while(iterator.hasNext()) {
						var typeClass = iterator.next();
						builder.append(typeClass.toString());
//						var generics = typeClass.getTypeVariable().getTypeDefinition().getGenericParameters();
//						
//						if (!generics.isEmpty()) {
//							builder.append("<");
//							
//							var gIterator = generics.iterator();
//							while(gIterator.hasNext()) {
//								var f = gIterator.next();
//								builder.append(f.getSymbol().get());
//							}
//							
//							builder.append(">");
//						}
						
						if (iterator.hasNext()) {
							builder.append(", ");
						}
					}
				}
				
				builder.append("{ \n")
				
				.append("public constructor() {} \n");
				
				
				
				builder.append("public duplicate() : Type { \n")
				.append("  return new ").append(name).append("(); \n")
				.append("}\n")
				
				.append("public getName() => \"" + node.getFullname() + "\"; \n")
				
				.append("protected loadMethods() : Sequence<Method> { \n")
				.append("    let  all = new LinkedList<Method>();\n ");
				for (var member : type.getAllMembers()) {
					if (member.isMethod()) {
						// TODO copy all flags
						builder.append("    all.add(new ReflectiveMethod(this,\"" + member.getName() + "\"));\n ");
					}
				}
				builder.append("    return all;\n");
				builder.append("}\n")
				.append("protected loadProperties():  Sequence<Property>  {\n")
				.append("    let  all = new LinkedList<Property>();\n ");
				for (var member : type.getAllMembers()) {
					if (member.isProperty()) {
						// TODO copy all flags
						builder.append("    all.add(new ReflectiveProperty(this,\"" + member.getName() + "\"));\n ");
					}
				}
				
				builder.append("    return all;\n");
				builder.append("}\n")
				.append("}");	
		
		return builder;
	}

}
