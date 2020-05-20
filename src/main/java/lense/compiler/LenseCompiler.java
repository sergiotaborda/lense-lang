package lense.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
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
import compiler.CompilationUnitSet;
import compiler.CompiledUnit;
import compiler.CompilerBackEnd;
import compiler.CompilerListener;
import compiler.CompilerMessage;
import compiler.FolderCompilationUnionSet;
import compiler.ListCompilationUnitSet;
import compiler.StringCompilationUnit;
import compiler.trees.TreeTransverser;
import lense.compiler.asm.ByteCodeTypeDefinitionReader;
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

    private FileLocations defineFileLocations(File moduleproject) throws IOException{
        File sources = new File(moduleproject, "source");

        if (!sources.exists()){
            listener.error(new CompilerMessage("No sources found. No folder " + sources.getAbsolutePath() + " exists"));
        }

        File modulesOut = new File(moduleproject, "compilation/" + nativeLanguageName + "/bin");
        if (!modulesOut.exists()){
            modulesOut.mkdirs();
        }

        File nativeSources = new File(moduleproject, "native/" + nativeLanguageName);


        File target = new File(moduleproject, "compilation/" + nativeLanguageName + "/target");


        // delete previous run
        if (!target.exists()){
            target.mkdirs();
        } else {
            Path directory = target.toPath();
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

            });
        }

        File modules = new File(moduleproject, "compilation/modules");

        return new FileLocations(target, nativeSources, sources, modules);
    }

    protected abstract void createModuleArchive(FileLocations locations, ModuleNode module, File base, Set<String> applications) throws IOException, FileNotFoundException;
    protected abstract void initCorePhase(CompositePhase corePhase, Map<String, File> nativeTypes, UpdatableTypeRepository typeContainer);
    protected abstract void collectNative(FileLocations fileLocations, Map<String, File> nativeTypes) throws IOException;
	protected abstract File resolveNativeFile(File folder, String name);
	
    /**
     * @param moduleproject
     * @throws IOException 
     */
    public void compileModuleFromDirectory(File moduleproject){

   
        Map<String, File> nativeTypes = new HashMap<>();

        listener.start();
        try {
        	
         	AstCompiler parser = new AstCompiler(new LenseLanguage());


            FileLocations locations = this.defineFileLocations(moduleproject);

           
            // compile lense files
            
            // first, compile the module information
            CompilationUnitSet moduleUnit = new FolderCompilationUnionSet(locations.getSourceFolder() , fileName -> fileName.equals("module.lense"));

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

            File base = null;
            if (globalRepository instanceof ClasspathRepository){
                base = ((ClasspathRepository)globalRepository).getBase();
                backendFactory.setClasspath(base);
            }
            
            // first compile target language native files

            collectNative(locations, nativeTypes);
   
            SemanticAnalysisPhase semantic = new SemanticAnalysisPhase(currentModuleRepository, listener);

            CompositePhase corePhase = new CompositePhase().add(semantic);

            initCorePhase (corePhase, nativeTypes, currentModuleRepository);

            // create dependency graph
            DependencyGraph graph = new DependencyGraph();

            // the root node is the module itself
            DependencyNode moduleNode = new DependencyNode(modulesList.get(0), module.getName());



            // load source files
            CompilationUnitSet unitSet = new FolderCompilationUnionSet(locations.getSourceFolder() , name -> !name.equals("module.lense") && name.endsWith(".lense"));

            Set<QualifiedNameNode> packages = new HashSet<>(); 
            Set<String> foundNames = new HashSet<>();
            Set<String> referencedNames = new HashSet<>();
            Set<String> applications = new HashSet<>();

            
      		ByteCodeTypeDefinitionReader reader = new ByteCodeTypeDefinitionReader(currentModuleRepository);
      		
      		if (selfCompilation) {
                currentModuleRepository.registerType(LenseTypeSystem.Any(), 0);
                currentModuleRepository.registerType(LenseTypeSystem.Nothing(), 0);
                currentModuleRepository.registerType(LenseTypeSystem.Void(), 0);
      		}


      		List<TypeDefinition> nativeTypesDefs = new ArrayList<TypeDefinition>(nativeTypes.size());
      		for( File target : nativeTypes.values()) {
          		TypeDefinition type = reader.readNative(target);
          		currentModuleRepository.registerType(type, type.getGenericParameters().size());
          		
          		nativeTypesDefs.add(type);
      		}
            
            // init foundnames with nothing because it is a non denotable
            foundNames.add("lense.core.lang.Nothing");
            
            CompositePhase prePhase = new CompositePhase()
                    .add(new ConstructorDesugarPhase(listener)) // TODO must be here ?
                    .add(new NameResolutionPhase(new PathPackageResolver(locations.getSourceFolder().toPath()), listener));

            trace("Creating dependency graph");
            
            
            String ref = "lense.core.lang.reflection.ReifiedArguments";
            //DependencyNode  reifiedArgumentsDependencyNode = new DependencyNode(null /* unkown at this point*/, ref);
            
            parser.parse(unitSet)
            .passBy(prePhase)
            .peek(compilationUnit -> {

            	 trace("Analysing unit file " + compilationUnit);
            	  
                //String packageName = sources.getAbsoluteFile().toPath().relativize(node.getUnit().getOrigin()).toString();
                //int pos = packageName.lastIndexOf('\\');

                //packageName = packageName.substring(0, pos).replace('\\', '.');
                UnitTypes t = (UnitTypes) compilationUnit.getAstRootNode();


                for(ClassTypeNode type : t.getTypes()){

                	trace("Analysing type " + type.getName());
               	  
                    foundNames.add(type.getName());

                    // the module depends on every type inside
                    
                    // check if the type already exists in the graph
                    Optional<DependencyNode> it = graph.findDependencyNode(type.getName());
                    DependencyNode dependency;
                    
                    if (it.isPresent()){
                        dependency = it.get();
                        dependency.setUnit(compilationUnit);
                        
                        if (type.getName().equals(ref)) {
                        	dependency.setUnit(compilationUnit);
                        }
                    } else {
                        dependency = new DependencyNode(compilationUnit, type.getName());
                    }

                    graph.addEdge(new DependencyRelation(DependencyRelationship.Module), dependency, moduleNode);


                    packages.add(new QualifiedNameNode(type.getSemanticContext().getCurrentPackageName()));
                    
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
            
         
            final CompilerBackEnd backend = backendFactory.create(locations);
  
    		GraphTransversor<DependencyRelation, DependencyNode> tt = new TopologicOrderTransversor<>();
               
            tt.addListener(new GraphTranverseListener<DependencyNode, DependencyRelation>() {

                @Override
                public void endVertex(VertexTraversalEvent<DependencyNode, DependencyRelation> e) {}

                @Override
                public void beginVertex(VertexTraversalEvent<DependencyNode, DependencyRelation> e) {
                    trace("Visiting : " + e.getVertex().getObject().getName());
                    CompiledUnit unit = e.getVertex().getObject().getCompiledUnit();

                    applyCompilation(nativeTypes, locations, corePhase, currentModuleRepository, backend, reader, unit);

  
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
                applyCompilation(nativeTypes, locations, corePhase, currentModuleRepository, backend, reader, any.map(v -> v.getObject().getCompiledUnit()).get());

                graph.removeVertex(any.get());
            }

            
            tt.transverse(graph, moduleNode);

            // produce package classes
            ListCompilationUnitSet all = new ListCompilationUnitSet();
            for(QualifiedNameNode pack : packages){

                StringBuilder builder = writePackage(pack);

                // TODO list types in package

                Path path = locations.getSourceFolder().toPath().resolve(pack.getName().replace('.', File.separatorChar)).resolve("Package$$Info.lense");
                all.add(new StringCompilationUnit(builder.toString(), path));
            }

            parser.parse(all)
            .passBy(new NameResolutionPhase(new PathPackageResolver(locations.getSourceFolder().toPath()), listener))
            .passBy(corePhase)
            .sendTo(backend);

            // produce module metadata and class

            StringBuilder builder = writeModule(module, packages);
            all = new ListCompilationUnitSet();
            all.add(new StringCompilationUnit(builder.toString(), locations.getSourceFolder().toPath().resolve(module.getName().replace('.', File.separatorChar)).resolve("Module$$Info.lense"))); // TODO specify package

            parser.parse(all)
            .passBy(new NameResolutionPhase(new PathPackageResolver(locations.getSourceFolder().toPath()), listener))
            .passBy(corePhase)
            .sendTo(backend);

            File modules = locations.getModulesFolder();
            if (!modules.exists()){
                modules.mkdirs();
            }

            createModuleArchive(locations, module, base, applications);

        } catch (Exception e) {
            e.printStackTrace();
            listener.error(new CompilerMessage(e.getMessage()));
        } finally {
            listener.end();
        }

    }
    
    private void applyCompilation(Map<String, File> nativeTypes, FileLocations locations, CompositePhase corePhase,
			ModuleCompilationScopeTypeRepository currentModuleRepository, final CompilerBackEnd backend,
			ByteCodeTypeDefinitionReader reader, CompiledUnit unit) {
		if (unit != null){
        	UnitTypes types = (UnitTypes)unit.getAstRootNode();
    		
        	for (ClassTypeNode type : types.getTypes()) {
        		
        		if (type.isNative()) {
        			
        			File nativeTypeFile = nativeTypes.get(type.getName());
                  
        			if (nativeTypeFile == null) {
    					if (type.getKind().isObject()) {
    						
    						String[] name = Strings.split(type.getName(), ".");
    						name[name.length - 1 ] = Strings.cammelToPascalCase(name[name.length - 1 ]);
    						
    						
    						nativeTypeFile =  resolveNativeFile (locations.getTargetFolder(), Strings.join(name, File.separator));
    					}
    					else 
    					{
    						String[] name = Strings.split(type.getName(), ".");
    						
    						nativeTypeFile =  resolveNativeFile (locations.getTargetFolder(), Strings.join(name, File.separator));
    					}
    					
    					if (nativeTypeFile == null) {
    						throw new CompilationError("Expected native file for type " + type.getName()  + " does not exist");
    					}
        			}
        			
        			try {
						TypeDefinition typeDef = reader.readNative(nativeTypeFile);
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
