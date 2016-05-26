/**
 * 
 */
package lense.compiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import compiler.AstCompiler;
import compiler.CompilationResult;
import compiler.CompilationResultSet;
import compiler.CompilationUnitSet;
import compiler.CompiledUnit;
import compiler.CompilerListener;
import compiler.CompilerMessage;
import compiler.FolderCompilationUnionSet;
import compiler.ListCompilationUnitSet;
import compiler.StringCompilationUnit;
import compiler.syntax.AstNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.ModuleImportNode;
import lense.compiler.ast.ModuleNode;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.UnitTypes;
import lense.compiler.crosscompile.java.JavaCompilerBackEndFactory;
import lense.compiler.crosscompile.java.JavaSourceBackEnd;
import lense.compiler.dependency.DependencyGraph;
import lense.compiler.dependency.DependencyNode;
import lense.compiler.dependency.DependencyRelation;
import lense.compiler.dependency.DependencyRelationship;
import lense.compiler.graph.EdgeTraversalEvent;
import lense.compiler.graph.GraphTransversor;
import lense.compiler.graph.GraphTranverseListener;
import lense.compiler.graph.TopologicOrderTransversor;
import lense.compiler.graph.VertexTraversalEvent;
import lense.compiler.phases.CompositePhase;
import lense.compiler.phases.DesugarPropertiesPhase;
import lense.compiler.phases.IntermediatyRepresentationPhase;
import lense.compiler.phases.JavalizePhase;
import lense.compiler.phases.NameResolutionPhase;
import lense.compiler.phases.SemanticAnaylisisPhase;
import lense.compiler.repository.ModuleRepository;
import lense.compiler.repository.TypeRepository;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.type.variable.RangeTypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.TypeSearchParameters;

/**
 * 
 */
public class LenseCompiler {

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

	};
	private CompilerBackEndFactory backendFactory = new  JavaCompilerBackEndFactory(); // JavaBackEndFactory(); //new  JavaSourceBackEnd(); //JavaBinaryBackEndFactory(); 
	private TypeRepository globalRepository;
	
	public LenseCompiler (TypeRepository globalRepository){
		// The global, even remote, repository
		this.globalRepository = globalRepository;
	}

	public void setCompilerListener(CompilerListener listener){
		this.listener = listener;
	}
	
	public void setCompilerPlatformBackEnd(CompilerBackEndFactory factory){
		this.backendFactory = factory;
	}
	/**
	 * @param moduleproject
	 * @throws IOException 
	 */
	public void compileModuleFromDirectory(File moduleproject){

		String nativeLanguage = "java";
		
		SemanticAnaylisisPhase semantic = new SemanticAnaylisisPhase(listener);
		DesugarPropertiesPhase desugarProperties = new DesugarPropertiesPhase(listener);
		IntermediatyRepresentationPhase  ir = new IntermediatyRepresentationPhase();
		JavalizePhase  jv = new JavalizePhase(listener);
		
		// TODO desugar native
		// TODO remove indexaccessnode from IR
		CompositePhase corePhase = new CompositePhase().add(semantic).add(desugarProperties).add(jv);//.add(ir);

		listener.start();
		try {

			File sources = new File(moduleproject, "source");

			if (!sources.exists()){
				listener.error(new CompilerMessage("No sources found. No folder " + sources.getAbsolutePath() +" exists"));
			}

			File modulesOut = new File(moduleproject, "compilation/" + nativeLanguage + "/bin");
			if (!modulesOut.exists()){
				modulesOut.mkdirs();
			}

			File nativeSources = new File(moduleproject, "native/" + nativeLanguage);
			
			
			File target = new File(moduleproject, "compilation/" + nativeLanguage + "/target");
			
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

			FileLocations locations = new FileLocations(target, nativeSources);
			
			// first compile java native files
			compileNative(locations);
			
			// compile lense files
			CompilationUnitSet moduleUnit = new FolderCompilationUnionSet(sources , name -> name.equals("module.lense"));

			AstCompiler parser = new AstCompiler(new LenseLanguage());
			

			final List<CompiledUnit> modulesList = parser.parse(moduleUnit).sendToList();

			if (modulesList.isEmpty()){
				listener.error(new CompilerMessage("Could not find module.lense file in the source. "));
			}

			ModuleNode module  = (ModuleNode) modulesList.get(0).getAstRootNode().getChildren().get(0);

	
			// The repository being mounted
			ModuleRepository currentModuleRepository = new ModuleRepository(module, globalRepository);


			for(compiler.syntax.AstNode n : module.getImports().getChildren()){
				ModuleImportNode ip = (ModuleImportNode)n;
				// import dependent modules from local repository
				// is they are not present , abort
				currentModuleRepository.importModule(ip.getQualifiedNameNode(), ip.getVersionNode());
			}

			DependencyGraph graph = new DependencyGraph();

			DependencyNode moduleNode = new DependencyNode(modulesList.get(0), module.getName());

			Set<QualifiedNameNode> packages = new HashSet<>(); 

			CompilationUnitSet unitSet = new FolderCompilationUnionSet(sources , name -> !name.equals("module.lense") && name.endsWith(".lense"));


			Set<String> foundNames = new HashSet<>();
			Set<String> referencedNames = new HashSet<>();

			parser.parse(unitSet)
			.passBy(new NameResolutionPhase(currentModuleRepository, new PathPackageResolver(sources.toPath()), listener))
			.peek(node -> {
				
				String packageName = node.getUnit().getOrigin().substring(sources.getAbsolutePath().length()+1);
				int pos = packageName.lastIndexOf('\\');
				
				packageName = packageName.substring(0, pos).replace('\\', '.');
				UnitTypes t = (UnitTypes) node.getAstRootNode();

				
				for(ClassTypeNode type : t.getTypes()){

					type.setName(packageName + '.' + type.getName());
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

					List<IntervalTypeVariable> typeParameters = new ArrayList<>();

					if (type.getGenerics() != null){

						for(AstNode n : type.getGenerics().getChildren()){
							GenericTypeParameterNode gnode = ((GenericTypeParameterNode)n);
							TypeNode tn = gnode.getTypeNode();
							typeParameters.add( new RangeTypeVariable(tn.getName(), gnode.getVariance(), LenseTypeSystem.Any() ,  LenseTypeSystem.Nothing() ));
						}

					}

					LenseTypeDefinition ltype = new LenseTypeDefinition(type.getName(), type.getKind(), null,typeParameters.toArray(new lense.compiler.type.variable.IntervalTypeVariable[0]));


					ltype = (LenseTypeDefinition) currentModuleRepository.registerType(ltype, ltype.getGenericParameters().size());

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
							System.out.println(dn.getName() + " strongly depends on " + imported.getName());
							graph.addEdge(new DependencyRelation(DependencyRelationship.Structural),  imported, dn);
						} else {
							System.out.println(dn.getName() + " referes by name " + imported.getName());
							// TODO validate it exists
							//graph.addEdge(new DependencyRelation(DependencyRelationship.Name),  imported, dn);
						}

						referencedNames.add(imported.getName());

					}
				}



			}).sendToList();

			referencedNames.removeAll(foundNames);

			for(Iterator<String> it = referencedNames.iterator(); it.hasNext(); ){
				if (currentModuleRepository.resolveType(new TypeSearchParameters(it.next(), 0)).isPresent()){
					it.remove();
				}
			}

			if (!referencedNames.isEmpty()){
				throw new CompilationError("Type " + referencedNames.stream().filter(r -> r.length() > 0).findFirst().get() + " was not found");
			}


			GraphTransversor<DependencyRelation, DependencyNode> tt = new TopologicOrderTransversor<>();
			tt.addListener(new GraphTranverseListener<DependencyNode, DependencyRelation>() {

				@Override
				public void endVertex(VertexTraversalEvent<DependencyNode, DependencyRelation> e) {

				}

				@Override
				public void beginVertex(VertexTraversalEvent<DependencyNode, DependencyRelation> e) {
					System.out.println("Visiting : " + e.getVertex().getObject().getName());
					new CompilationResultSet( new CompilationResult(e.getVertex().getObject().getCompiledUnit()))
					.passBy(corePhase).sendTo( backendFactory.create(locations));
					
					System.out.println("Visited : " + e.getVertex().getObject().getName());
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

			// TODO organize units in dependency order

			// compile souce files and read packages
			//			compiler.parse(unitSet).peek(new OutToJavaSource(target)).stream().forEach( cr -> {  // TODO add package to type from folder
			//				if (!cr.isError()){
			//					UnitTypes unit = (UnitTypes)cr.getCompiledUnit().getAstRootNode();
			//
			//					ClassTypeNode type = (ClassTypeNode) unit.getChildren().get(0);
			//
			//					QualifiedNameNode qn = new QualifiedNameNode(type.getName());
			//
			//					QualifiedNameNode pack = qn.getPrevious();
			//					if (!pack.getName().startsWith(module.getName())){
			//						throw new CompilationError("Package '" + pack + "' name in unit '" + type.getName() + "' does not beging with module name '" + module.getName() + "')");
			//					}
			//					packages.add(pack);
			//				}
			//			});


			// produce package classes
			ListCompilationUnitSet all = new ListCompilationUnitSet();
			for(QualifiedNameNode pack : packages){

				StringBuilder builder = new StringBuilder("import lense.core.lang.reflection.Package; import lense.core.lang.String;")
				.append("public class Package$$Info implements Package { \n")
				.append(" public String getName() {\n")
				.append("	return \"").append(pack).append("\" ;\n")
				.append("}\n")
				.append("}\n");
				
				// TODO list types in package

				all.add(new StringCompilationUnit(builder.toString(), sources.toPath().resolve(pack.getName().replace('.', '/')).toString()+ "/Package$$Info.lense")); // TODO specify package
			}

			parser.parse(all)
			.passBy(new NameResolutionPhase(currentModuleRepository, new PathPackageResolver(sources.toPath()), listener))
			.passBy(corePhase)
			.sendTo(backendFactory.create(locations));
			
			// produce module metadata and class
			
			Properties p = new Properties();

			p.put("module.name", module.getName());
			p.put("module.version", module.getVersion().toString());

			File moduleProperties = new File(target, "module.properties");
			p.store(new FileOutputStream(moduleProperties), "Lense module definition");

			StringBuilder builder = new StringBuilder("import lense.core.lang.reflection.Module; import lense.core.lang.Version; import lense.core.collections.Sequence; import lense.core.collections.LinkedList; import lense.core.lang.reflection.Package; ");
			
			int i=1;
			for(QualifiedNameNode pack : packages){ 
				builder.append(" import ").append(pack).append(".Package$$Info as Pack").append(i).append(";\n");
				i++;
			}
			
			builder.append("public class ").append("Module$$Info implements Module { ");

			builder.append("public Version getVersion(){");
			builder.append("	return new Version(\"").append(module.getVersion()).append("\");");
			builder.append("}");
			builder.append("public Sequence<Package> getPackages(){");
			builder.append("	var LinkedList<Package> all = new LinkedList<Package>(); ");
			
			for(i =0; i < packages.size(); i++){ 
				builder.append("	all.add(new Pack").append(i+1).append("());\n");
			}
			
			builder.append(" 	return all;");
			builder.append("}");
			builder.append("}");
			all = new ListCompilationUnitSet();
			all.add(new StringCompilationUnit(builder.toString(), sources.toPath().resolve(module.getName().replace('.', '/')).toString() + "/Module$$Info.lense")); // TODO specify package

			
			parser.parse(all)
			.passBy(new NameResolutionPhase(currentModuleRepository, new PathPackageResolver(sources.toPath()), listener))
			.passBy(corePhase)
			.sendTo(backendFactory.create(locations  ));
			 
			// TODO zip it to a jar file and delete all singular files.
		} catch (Exception e) {
			e.printStackTrace();
			listener.error(new CompilerMessage(e.getMessage()));
		} finally {
			listener.end();
		}

	}

	private void compileNative(FileLocations fileLocations) throws IOException {
		List<File> files = new LinkedList<>();

		final Path rootDir = fileLocations.getNativeFolder().toPath();
		final Path targetDir = fileLocations.getTargetFolder().toPath();

		Files.walkFileTree(rootDir, new FileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes atts) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes mainAtts)
					throws IOException {

				if (path.toString().endsWith(".java")){
					files.add(path.toFile());
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path path,
					IOException exc) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path path, IOException exc)
					throws IOException {
				exc.printStackTrace();

				return path.equals(rootDir)? FileVisitResult.TERMINATE:FileVisitResult.CONTINUE;
			}
		});

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

		Iterable<? extends JavaFileObject> compilationUnits1 = fileManager.getJavaFileObjectsFromFiles(files);
		compiler.getTask(new PrintWriter(System.err), fileManager, null, null, null, compilationUnits1).call();

		for (File n : files){
			String packageFile = n.getAbsolutePath().substring(rootDir.toString().length());
			int pos = packageFile.indexOf(".java");
			packageFile = packageFile.substring(0, pos) + ".class";
			File source = new File(n.getParentFile(), n.getName().substring(0,  n.getName().length() - 5) + ".class");
			File target = new File(fileLocations.getTargetFolder(),packageFile);
			
			Files.move(source.toPath(), target.toPath());

		}
	}


}
