package lense.compiler;

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
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.ModuleNode;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.UnitTypes;
import lense.compiler.dependency.DependencyGraph;
import lense.compiler.dependency.DependencyNode;
import lense.compiler.dependency.DependencyRelation;
import lense.compiler.dependency.DependencyRelationship;
import lense.compiler.graph.EdgeTraversalEvent;
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
import lense.compiler.type.TypeNotFoundException;
import lense.compiler.type.variable.RangeTypeVariable;
import lense.compiler.type.variable.TypeVariable;
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
    protected abstract void compileNative(FileLocations fileLocations, Map<String, File> nativeTypes, UpdatableTypeRepository typeContainer) throws IOException;

    /**
     * @param moduleproject
     * @throws IOException 
     */
    public void compileModuleFromDirectory(File moduleproject){


        Map<String, File> nativeTypes = new HashMap<>();

        ConstructorDesugarPhase constructorDesugar = new ConstructorDesugarPhase(listener);
        SemanticAnalysisPhase semantic = new SemanticAnalysisPhase(listener);

        CompositePhase corePhase = new CompositePhase().add(semantic);


        listener.start();
        try {

            FileLocations locations = this.defineFileLocations(moduleproject);

           
            // compile lense files
            CompilationUnitSet moduleUnit = new FolderCompilationUnionSet(locations.getSourceFolder() , fileName -> fileName.equals("module.lense"));

            AstCompiler parser = new AstCompiler(new LenseLanguage());


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

       
         
            for(ModuleDescription requiredModule : moduleDescriptor.getRequiredModules()){

                // import dependent modules from local repository
                // is they are not present , abort
                Optional<ModuleTypeContents> otherModule = globalRepository.resolveModuleByNameAndVersion(requiredModule.getModuleIdentifier());


                if (!otherModule.isPresent()){
                    throw new CompilationError("Cannot import module " + requiredModule.getName() + " version " + requiredModule.getVersion() + ". Is it in the local repository?");
                } else {
                    currentModuleRepository.addRequiredModule(otherModule.get());
                }
                
            }

            File base = null;
            if (globalRepository instanceof ClasspathRepository){
                base = ((ClasspathRepository)globalRepository).getBase();
                backendFactory.setClasspath(base);
            }
            
            // first compile target language native files

            compileNative(locations, nativeTypes, currentModuleRepository);
   
            initCorePhase (corePhase, nativeTypes, currentModuleRepository);

            DependencyGraph graph = new DependencyGraph();

            DependencyNode moduleNode = new DependencyNode(modulesList.get(0), module.getName());

            Set<QualifiedNameNode> packages = new HashSet<>(); 

            CompilationUnitSet unitSet = new FolderCompilationUnionSet(locations.getSourceFolder() , name -> !name.equals("module.lense") && name.endsWith(".lense"));


            Set<String> foundNames = new HashSet<>();
            Set<String> referencedNames = new HashSet<>();

            Set<String> applications = new HashSet<>();

            CompositePhase prePhase = new CompositePhase()
                    .add(constructorDesugar)
                    .add(new NameResolutionPhase(currentModuleRepository, new PathPackageResolver(locations.getSourceFolder().toPath()), listener));

            trace("Creating dependency graph");
            
            parser.parse(unitSet)
            .passBy(prePhase)
            .peek(node -> {

            	 trace("Analysing unit file " + node);
            	  
                //String packageName = sources.getAbsoluteFile().toPath().relativize(node.getUnit().getOrigin()).toString();
                //int pos = packageName.lastIndexOf('\\');

                //packageName = packageName.substring(0, pos).replace('\\', '.');
                UnitTypes t = (UnitTypes) node.getAstRootNode();


                for(ClassTypeNode type : t.getTypes()){

                	 trace("Analysing type " + type.getName());
               	  

                    foundNames.add(type.getName());

                    // the module depends on every type inside
                    Optional<DependencyNode> it = graph.findDependencyNode(type.getName());
                    DependencyNode dn;
                    if (it.isPresent()){
                        dn = it.get();
                        dn.setUnit(node);
                    }else {
                        dn = new DependencyNode(node, type.getName());
                    }

                    List<TypeVariable> typeParameters = new ArrayList<>();

                    if (type.getGenerics() != null){

                        for(AstNode n : type.getGenerics().getChildren()){
                            GenericTypeParameterNode gnode = ((GenericTypeParameterNode)n);
                            TypeNode tn = gnode.getTypeNode();
                            typeParameters.add( new RangeTypeVariable(tn.getName(), gnode.getVariance(), LenseTypeSystem.Any() ,  LenseTypeSystem.Nothing() ));
                        }

                    }

                    LenseTypeDefinition ltype = null;

                    try {
                        ltype = (LenseTypeDefinition)type.getSemanticContext().typeForName(type.getScanPosition(), type.getName(), type.getGenericParametersCount());

                    } catch (TypeNotFoundException e) {
                        ltype = new LenseTypeDefinition(type.getName(), type.getKind(), null,typeParameters);
                        ltype = (LenseTypeDefinition) currentModuleRepository.registerType(ltype, ltype.getGenericParameters().size());
                    }



                    graph.addEdge(new DependencyRelation(DependencyRelationship.Module), dn, moduleNode);


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
                            trace(dn.getName() + " strongly depends on " + imported.getName());
                            if (imported.getName().equals("lense.core.system.ConsoleApplication")){
                                applications.add(dn.getName());
                            }
                            graph.addEdge(new DependencyRelation(DependencyRelationship.Structural),  imported, dn);
                        } else if (dn.getName().equals(imported.getName())){
                            trace(dn.getName() + " referes by to it self ");
                            continue;
                        } else {
                            // trace(dn.getName() + " referes by name " + imported.getName());
                            // TODO validate it exists
                            //graph.addEdge(new DependencyRelation(DependencyRelationship.Name),  imported, dn);
                        }

                        referencedNames.add(imported.getName());

                    }
                }



            }).sendToList();

            referencedNames.removeAll(foundNames);

            for(Iterator<String> it = referencedNames.iterator(); it.hasNext(); ){
                if (currentModuleRepository.resolveType(new TypeSearchParameters(it.next())).isPresent()){
                    it.remove();
                }
            }

            if (!referencedNames.isEmpty()){
                throw new CompilationError("Type " + referencedNames.stream().filter(r -> r.length() > 0).findFirst().get() + " was not found");
            }

            trace("Compiling graph");
            
            GraphTransversor<DependencyRelation, DependencyNode> tt = new TopologicOrderTransversor<>();
            final CompilerBackEnd backend = backendFactory.create(locations);
            tt.addListener(new GraphTranverseListener<DependencyNode, DependencyRelation>() {

                @Override
                public void endVertex(VertexTraversalEvent<DependencyNode, DependencyRelation> e) {

                }

                @Override
                public void beginVertex(VertexTraversalEvent<DependencyNode, DependencyRelation> e) {
                    trace("Visiting : " + e.getVertex().getObject().getName());
                    CompiledUnit unit = e.getVertex().getObject().getCompiledUnit();
                    if (unit != null){
                        new CompilationResultSet( new CompilationResult(unit))
                        .passBy(corePhase).sendTo( backend);

                    }

                    trace("Visited : " + e.getVertex().getObject().getName());
                }
                @Override
                public void endEdgeTraversed(
                        EdgeTraversalEvent<DependencyRelation, DependencyNode> e) {

                }
                @Override
                public void beginEdgeTraversed(
                        EdgeTraversalEvent<DependencyRelation, DependencyNode> e) {

                }
            });
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
            .passBy(new NameResolutionPhase(currentModuleRepository, new PathPackageResolver(locations.getSourceFolder().toPath()), listener))
            .passBy(corePhase)
            .sendTo(backend);

            // produce module metadata and class

            StringBuilder builder = writeModule(module, packages);
            all = new ListCompilationUnitSet();
            all.add(new StringCompilationUnit(builder.toString(), locations.getSourceFolder().toPath().resolve(module.getName().replace('.', File.separatorChar)).resolve("Module$$Info.lense"))); // TODO specify package

            parser.parse(all)
            .passBy(new NameResolutionPhase(currentModuleRepository, new PathPackageResolver(locations.getSourceFolder().toPath()), listener))
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
        .append(" public equalsTo( other : Any) : Boolean {\n")
        .append("   return false;\n")
        .append("}\n")
        .append(" public hashValue() : HashValue {\n")
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
                .append(" public equalsTo( other: Any) : Boolean {\n")
                .append("   return false;\n")
                .append("}\n")
                .append(" public hashValue() : HashValue {\n")
                .append("   return getName().hashValue();\n")
                .append("}\n")
                .append("}\n");
        return builder;
    }
}
