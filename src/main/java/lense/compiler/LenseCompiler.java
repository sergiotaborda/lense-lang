package lense.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import lense.compiler.ast.UnitTypes;
import lense.compiler.dependency.CyclicDependencyResolver;
import lense.compiler.dependency.DependencyGraph;
import lense.compiler.dependency.DependencyNode;
import lense.compiler.dependency.DependencyRelation;
import lense.compiler.dependency.DependencyRelationship;
import lense.compiler.graph.EdgeTraversalEvent;
import lense.compiler.graph.Graph.Vertex;
import lense.compiler.graph.GraphTransversor;
import lense.compiler.graph.GraphTranverseListener;
import lense.compiler.graph.TopologicOrderTransversor;
import lense.compiler.graph.VertexTraversalEvent;
import lense.compiler.modules.EditableModuleDescriptor;
import lense.compiler.modules.ModuleDescription;
import lense.compiler.modules.ModuleTypeContents;
import lense.compiler.modules.ModulesRepository;
import lense.compiler.phases.CompositePhase;
import lense.compiler.phases.ConstructorDesugarPhase;
import lense.compiler.phases.NameResolutionPhase;
import lense.compiler.phases.OtimizationPhase;
import lense.compiler.phases.SemanticAnalysisPhase;
import lense.compiler.repository.ClasspathRepository;
import lense.compiler.repository.ModuleCompilationScopeTypeRepository;
import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.TypeSearchParameters;
import lense.compiler.utils.Strings;

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

    public LenseCompiler (String nativeLanguageName, ModulesRepository globalRepository,CompilerBackEndFactory backendFactory){
        // The global, even remote, repository
        this.globalRepository = globalRepository;
        this.nativeLanguageName = nativeLanguageName;
        this.backendFactory = backendFactory;
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

    protected abstract void createModuleArchive(FileLocations locations, ModuleNode module, SourceFolder base, Set<String> applications) throws IOException, FileNotFoundException;
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
		
//		if(temp.exists()) {
//			temp.delete();
//		}
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
		    e.printStackTrace();
		    listener.error(new CompilerMessage(e.getMessage()));
		 } finally {
		    listener.end();
		}
	 }
	 
 	 protected abstract List<TypeDefinition> extactTypeDefinitionFronNativeType(UpdatableTypeRepository currentTypeRepository , Collection<SourceFile> files) throws IOException;
 	
     public void compileCompilationUnitSet(CompilationUnitSet moduleUnit,CompilationUnitSet unitSet, FileLocations locations){

		   
        Map<String, SourceFile> nativeTypes = new HashMap<>();

        listener.start();
        try {
        	
         	AstCompiler parser = new AstCompiler(new LenseLanguage());

            // compile lense files
            
            // first, compile the module information
        
            final List<CompiledUnit> modulesList = parser.parse(moduleUnit).sendToList();

            if (modulesList.isEmpty()){
                listener.error(new CompilerMessage("Could not find module.lense file in the source. "));
            }

            // locate module definition
            ModuleNode module  = (ModuleNode) modulesList.get(0).getAstRootNode().getChildren().get(0);

            EditableModuleDescriptor moduleDescriptor = new EditableModuleDescriptor();

            TreeTransverser.transverse(module, new ModuleDescriptionReaderVisitor(moduleDescriptor));

            boolean selfCompilation = moduleDescriptor.getName().equals("lense.core");
            
            if (!selfCompilation){ // if not compiling lense core
                Optional<ModuleDescription> coreModule = moduleDescriptor.getModuleByName("lense.core");

                if (coreModule.isPresent()){
                    // TODO match the current version for compatabiliy and/or take the SDK for that version
                } else {
                    // assume the current version is the target
                }
            }

            // Define the repository being mounted
            ModuleCompilationScopeTypeRepository currentModuleRepository =  new ModuleCompilationScopeTypeRepository();


            // import dependent modules from local repository
            for(ModuleDescription requiredModule : moduleDescriptor.getRequiredModules()){

                
                Optional<ModuleTypeContents> otherModule = globalRepository.resolveModuleByNameAndVersion(requiredModule.getModuleIdentifier());

             // is they are not present , abort
                currentModuleRepository.addRequiredModule(otherModule.orElseThrow(
                		() -> new CompilationError("Cannot import module " + requiredModule.getName() + " version " + requiredModule.getVersion() + ". Is it in the local repository?"))
                );
                
                
            }

            SourceFolder base = null;
            if (globalRepository instanceof ClasspathRepository){
                base = ((ClasspathRepository)globalRepository).getBase();
                backendFactory.setClasspath(base);
            }
            
            // first compile target language native files

            collectNative(locations, nativeTypes);
   
            CompositePhase corePhase = new CompositePhase()
            		.add(new SemanticAnalysisPhase(currentModuleRepository, listener))
            		.add(new OtimizationPhase(listener));

            initCorePhase (corePhase, nativeTypes, currentModuleRepository);

            // create dependency graph
            DependencyGraph graph = new DependencyGraph();

            // the root node is the module itself
            DependencyNode moduleNode = new DependencyNode(modulesList.get(0), module.getName());

            // load source files

            Set<QualifiedNameNode> packages = new HashSet<>(); 
            Set<String> foundNames = new HashSet<>();
            Set<String> referencedNames = new HashSet<>();
            Set<String> applications = new HashSet<>();

            
//      		ByteCodeTypeDefinitionReader reader = new ByteCodeTypeDefinitionReader(currentModuleRepository);
      		
      		if (selfCompilation) {
                currentModuleRepository.registerType(LenseTypeSystem.Any(), 0);
                currentModuleRepository.registerType(LenseTypeSystem.Nothing(), 0);
                currentModuleRepository.registerType(LenseTypeSystem.Void(), 0);
      		}

      		List<TypeDefinition> nativeTypesDefs = this.extactTypeDefinitionFronNativeType(currentModuleRepository, nativeTypes.values());

            // init foundnames with nothing because it is a non denotable
            foundNames.add("lense.core.lang.Nothing");
            
            CompositePhase prePhase = new CompositePhase()
                    .add(new ConstructorDesugarPhase(listener)) // TODO must be here ?
                    .add(new NameResolutionPhase(new PathPackageResolver(locations.getSourceFolder().getPath()), listener));

            trace("Creating dependency graph");
            
            
            String ref = "lense.core.lang.reflection.ReifiedArguments";
           
            parser.parse(unitSet)
            .passBy(prePhase)
            .peek(compilationUnit -> {

            	 trace("Analysing unit file " + compilationUnit);
            	  
                //String packageName = sources.getAbsoluteFile().toPath().relativize(node.getUnit().getOrigin()).toString();
                //int pos = packageName.lastIndexOf('\\');

                //packageName = packageName.substring(0, pos).replace('\\', '.');
                UnitTypes t = (UnitTypes) compilationUnit.getAstRootNode();


                for(ClassTypeNode type : t.getTypes()){

                	trace("Analysing type " + type.getFullname());
               	  
                    foundNames.add(type.getFullname());

                    // the module depends on every type inside
                    
                    // check if the type already exists in the graph
                    Optional<DependencyNode> it = graph.findDependencyNode(type.getFullname());
                    DependencyNode dependency;
                    
                    if (it.isPresent()){
                        dependency = it.get();
                        dependency.setUnit(compilationUnit);
                        
                        if (type.getFullname().equals(ref)) {
                        	dependency.setUnit(compilationUnit);
                        }
                    } else {
                        dependency = new DependencyNode(compilationUnit, type.getFullname());
                    }

                    graph.addEdge(new DependencyRelation(DependencyRelationship.Module), dependency, moduleNode);


                   if(type.getSemanticContext().getCurrentPackageName() != null && type.getSemanticContext().getCurrentPackageName().length() > 0) {
                	   packages.add(new QualifiedNameNode(type.getSemanticContext().getCurrentPackageName()));
                   }
                    
                   
                    for(Import imp : type.imports()){
                        String name = imp.getTypeName().getName();
                        it = graph.findDependencyNode(name);
                        DependencyNode imported;
                        if (it.isPresent()){
                            imported = it.get();
                        }else {
                            imported = new DependencyNode(null, name);
                        }

                        if (imp.isMemberCalled()){
                            trace(dependency.getName() + " strongly depends on " + imported.getName());
                            if (imported.getName().equals("lense.core.system.ConsoleApplication")){
                                applications.add(dependency.getName());
                            }
                            graph.addEdge(new DependencyRelation(DependencyRelationship.Structural),  imported, dependency);
                        } else if (dependency.getName().equals(imported.getName())){
                            trace(dependency.getName() + " referes by to it self ");
                            continue;
                        } 

                        referencedNames.add(imported.getName());

                    }
                 
                }



            }).sendToList();


            referencedNames.removeAll(foundNames);
            
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
  
    		GraphTransversor<DependencyRelation, DependencyNode> tt = new TopologicOrderTransversor<>();
               
            tt.addListener(new GraphTranverseListener<DependencyNode, DependencyRelation>() {

                @Override
                public void endVertex(VertexTraversalEvent<DependencyNode, DependencyRelation> e) {}

                @Override
                public void beginVertex(VertexTraversalEvent<DependencyNode, DependencyRelation> e) {
                    trace("Visiting : " + e.getVertex().getObject().getName());
                    CompiledUnit unit = e.getVertex().getObject().getCompiledUnit();

                    if (unit != null && !unit.getProperty("visited", Boolean.class).orElse(false)) {
                    	
                       applyCompilation(nativeTypes, locations, corePhase, currentModuleRepository, backend, unit);
                       unit.setProperty("visited", Boolean.TRUE);
                    } 
                   
   
                    trace("Visited : " + e.getVertex().getObject().getName());
                }

				
                @Override
                public void endEdgeTraversed(EdgeTraversalEvent<DependencyRelation, DependencyNode> e) {}
                
                @Override
                public void beginEdgeTraversed( EdgeTraversalEvent<DependencyRelation, DependencyNode> e) {}
            });
            
           
            // apply any first
            Optional<Vertex<DependencyNode, DependencyRelation>> any = graph.getVertices().stream().filter(v -> v.getObject().getName().equals(LenseTypeSystem.Any().getName())).findAny();
            
            if (selfCompilation && any.isPresent()) {
                applyCompilation(nativeTypes, locations, corePhase, currentModuleRepository, backend, any.map(v -> v.getObject().getCompiledUnit()).get());

                graph.removeVertex(any.get());
            }

            
            tt.transverse(graph, moduleNode);

            // produce package classes
            ListCompilationUnitSet all = new ListCompilationUnitSet();
            for(QualifiedNameNode pack : packages){

                StringBuilder builder = writePackage(pack);

                // TODO list types in package

                var path = locations.getSourceFolder().folder(PackageSourcePathUtils.fromPackageName(pack.getName())).file("Package$$Info.lense").getPath();
           
                all.add(new StringCompilationUnit(builder.toString(), path));
            }

            parser.parse(all)
            .passBy(new NameResolutionPhase(new PathPackageResolver(locations.getSourceFolder().getPath()), listener))
            .passBy(corePhase)
            .sendTo(backend);

            // produce module metadata and class

            StringBuilder builder = writeModule(module, packages);
            all = new ListCompilationUnitSet();
            all.add(new StringCompilationUnit(builder.toString(), locations.getSourceFolder().folder(PackageSourcePathUtils.fromPackageName(module.getName())).file("Module$$Info.lense").getPath())); // TODO specify package

            parser.parse(all)
            .passBy(new NameResolutionPhase(new PathPackageResolver(locations.getSourceFolder().getPath()), listener))
            .passBy(corePhase)
            .sendTo(backend);

            var modules = locations.getModulesFolder();
            if (!modules.exists()){
                modules.ensureExists();
            }

            createModuleArchive(locations, module, base, applications);

        } catch (Exception e) {
            e.printStackTrace();
            listener.error(new CompilerMessage(e.getMessage()));
        } finally {
            listener.end();
        }

    }
    
    private void applyCompilation(Map<String, SourceFile> nativeTypes, FileLocations locations, CompositePhase corePhase,
			ModuleCompilationScopeTypeRepository currentModuleRepository, final CompilerBackEnd backend,
		   CompiledUnit unit) {
		if (unit != null){
        	UnitTypes types = (UnitTypes)unit.getAstRootNode();
    		
        	for (ClassTypeNode type : types.getTypes()) {
        		
        		if (type.isNative()) {
        			
        			SourceFile nativeTypeFile = nativeTypes.get(type.getFullname());
                  
        			if (nativeTypeFile == null) {
    					if (type.getKind().isObject()) {
    						
    						String[] name = Strings.split(type.getFullname(), ".");
    						name[name.length - 1 ] = Strings.cammelToPascalCase(name[name.length - 1 ]);
    						
    						
    						nativeTypeFile =  resolveNativeFile (locations.getTargetFolder(), Strings.join(name, File.separator));
    					}
    					else 
    					{
    						String[] name = Strings.split(type.getFullname(), ".");
    						
    						nativeTypeFile =  resolveNativeFile (locations.getTargetFolder(), Strings.join(name, File.separator));
    					}
    					
    					if (nativeTypeFile == null) {
    						throw new CompilationError("Expected native file for type " + type.getFullname()  + " does not exist");
    					}
        			}
        			
        			try {
        				TypeDefinition typeDef = this.extactTypeDefinitionFronNativeType(currentModuleRepository, Arrays.asList(nativeTypeFile)).get(0);

						typeDef = currentModuleRepository.registerType(typeDef, typeDef.getGenericParameters().size());
						
						type.setTypeDefinition((LenseTypeDefinition)typeDef);

					} catch (IOException e1) {
						throw new RuntimeException(e1);
					}
        		}
        	}
        	
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
        builder.append("    var  all : LinkedList<Package>= new LinkedList<Package>();\n ");

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

    private StringBuilder writePackage(QualifiedNameNode pack) {
        StringBuilder builder = new StringBuilder("import lense.core.lang.reflection.Package; import lense.core.lang.Any; import lense.core.lang.String; import lense.core.lang.Boolean;")
                .append("public class Package$$Info implements Package { \n")
                .append(" public constructor ();")
                .append(" public getName() : String {\n")
                .append("   return \"").append(pack).append("\" ;\n")
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
}
