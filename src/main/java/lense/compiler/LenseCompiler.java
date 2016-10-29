/**
 * 
 */
package lense.compiler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
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
import lense.compiler.crosscompile.java.JavalizePhase;
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
import lense.compiler.phases.ConstructorDesugarPhase;
import lense.compiler.phases.DesugarPropertiesPhase;
import lense.compiler.phases.IntermediatyRepresentationPhase;
import lense.compiler.phases.NameResolutionPhase;
import lense.compiler.phases.SemanticAnaylisisPhase;
import lense.compiler.repository.ClasspathRepository;
import lense.compiler.repository.ModuleRepository;
import lense.compiler.repository.TypeRepository;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeDefinition;
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

	private JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

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
		Map<String, File> nativeTypes = new HashMap<>();

		ConstructorDesugarPhase constructorDesugar = new ConstructorDesugarPhase(listener);
		SemanticAnaylisisPhase semantic = new SemanticAnaylisisPhase(listener);
		DesugarPropertiesPhase desugarProperties = new DesugarPropertiesPhase(listener);
		IntermediatyRepresentationPhase  ir = new IntermediatyRepresentationPhase();
		JavalizePhase  jv = new JavalizePhase(listener,nativeTypes);

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

			compileNative(locations, nativeTypes);

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
				ModuleRepository repo = currentModuleRepository.importModule(ip.getQualifiedNameNode(), ip.getVersionNode());
			}

			File base = null;
			if (globalRepository instanceof ClasspathRepository){
				base = ((ClasspathRepository)globalRepository).getBase();
				backendFactory.setClasspath(base);
			}

			DependencyGraph graph = new DependencyGraph();

			DependencyNode moduleNode = new DependencyNode(modulesList.get(0), module.getName());

			Set<QualifiedNameNode> packages = new HashSet<>(); 

			CompilationUnitSet unitSet = new FolderCompilationUnionSet(sources , name -> !name.equals("module.lense") && name.endsWith(".lense"));


			Set<String> foundNames = new HashSet<>();
			Set<String> referencedNames = new HashSet<>();

			Set<String> applications = new HashSet<>();

			CompositePhase prePhase = new CompositePhase()
					.add(constructorDesugar)
					.add(new NameResolutionPhase(currentModuleRepository, new PathPackageResolver(sources.toPath()), listener));
			
			parser.parse(unitSet)
			.passBy(prePhase)
			.peek(node -> {

				//String packageName = sources.getAbsoluteFile().toPath().relativize(node.getUnit().getOrigin()).toString();
				//int pos = packageName.lastIndexOf('\\');

				//packageName = packageName.substring(0, pos).replace('\\', '.');
				UnitTypes t = (UnitTypes) node.getAstRootNode();


				for(ClassTypeNode type : t.getTypes()){


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

					LenseTypeDefinition ltype = null;

					try {
						ltype = (LenseTypeDefinition)type.getSemanticContext().typeForName(type.getName(), type.getGenericParametersCount());

					} catch (TypeNotPresentException e) {
						ltype = new LenseTypeDefinition(type.getName(), type.getKind(), null,typeParameters.toArray(new lense.compiler.type.variable.IntervalTypeVariable[0]));
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
							System.out.println(dn.getName() + " strongly depends on " + imported.getName());
							if (imported.getName().equals("lense.core.system.ConsoleApplication")){
								applications.add(dn.getName());
							}
							graph.addEdge(new DependencyRelation(DependencyRelationship.Structural),  imported, dn);
						} else if (dn.getName().equals(imported.getName())){
							System.out.println(dn.getName() + " referes by to it self ");
							continue;
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
				if (currentModuleRepository.resolveType(new TypeSearchParameters(it.next())).isPresent()){
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
					CompiledUnit unit = e.getVertex().getObject().getCompiledUnit();
					if (unit != null){
						new CompilationResultSet( new CompilationResult(unit))
						.passBy(corePhase).sendTo( backendFactory.create(locations));

					}

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

			// produce package classes
			ListCompilationUnitSet all = new ListCompilationUnitSet();
			for(QualifiedNameNode pack : packages){

				StringBuilder builder = writePackage(pack);

				// TODO list types in package

				Path path = sources.toPath().resolve(pack.getName().replace('.', File.separatorChar)).resolve("Package$$Info.lense");
				all.add(new StringCompilationUnit(builder.toString(), path));
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

			StringBuilder builder = writeModule(module, packages);
			all = new ListCompilationUnitSet();
			all.add(new StringCompilationUnit(builder.toString(), sources.toPath().resolve(module.getName().replace('.', File.separatorChar)).resolve("Module$$Info.lense"))); // TODO specify package

	


			parser.parse(all)
			.passBy(new NameResolutionPhase(currentModuleRepository, new PathPackageResolver(sources.toPath()), listener))
			.passBy(corePhase)
			.sendTo(backendFactory.create(locations  ));

			File modules = new File(moduleproject, "compilation/modules");

			if (!modules.exists()){
				modules.mkdirs();
			}
			
			Manifest manifest = new Manifest();
			manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
			
			// add bootstrap class
			if (!applications.isEmpty()){

				if (applications.size() > 1){
					throw new CompilationError("More than one Application was found. Every module can only have a maximum of 1 application");
				}
				String mainType = applications.iterator().next();
				
				int pos = mainType.lastIndexOf('.');
				String pack = mainType.substring(0, pos);
				
				builder = writeBootstrap(mainType, pack);

				File sourceFile = new File(locations.getTargetFolder().getAbsolutePath() + "/" + pack.replace('.', '/') , "Bootstrap.java");
				try(FileWriter writer = new FileWriter(sourceFile)){
					writer.write(builder.toString());
					writer.close();
				}
				
				StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
				Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(sourceFile));
				
				List<File> classPath = new ArrayList<>(2);
				if (base != null){
					for (File jar : base.listFiles(f -> f.getName().endsWith(".jar"))){
						classPath.add(jar);
					}
				}
				classPath.add(locations.getTargetFolder());
				fileManager.setLocation(StandardLocation.CLASS_PATH, classPath);

			    compiler.getTask(new PrintWriter(System.err),fileManager, null, null, null,  compilationUnits).call();

			    manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, pack + ".Bootstrap");
				
			}

			
			File file = new File (modules, module.getName() + ".jar");
			
			createJar(p,locations.getTargetFolder(), file,manifest);

		} catch (Exception e) {
			e.printStackTrace();
			listener.error(new CompilerMessage(e.getMessage()));
		} finally {
			listener.end();
		}

	}

	private StringBuilder writeBootstrap(String applicationType , String pack) {
		
		
		StringBuilder builder = new StringBuilder("package ").append(pack).append(";\n")
				.append("import lense.core.system.ConsoleApplication;\n")
				.append("import lense.core.collections.Array;\n")
				.append("public class Bootstrap {\n")
				.append("public static void main(String[] args) {\n")
				.append("		ConsoleApplication app = ").append(applicationType).append(".constructor();\n")
				.append("		app.setArguments(Array.fromNative(args, s -> lense.core.lang.String.valueOfNative(s)));\n")
				.append("		app.onStart();\n")
				.append("	}")
				.append("}");


		return builder;
	}

	private void createJar(Properties properties, File source, File output, Manifest manifest) throws IOException {
	
		JarOutputStream target = new JarOutputStream(new FileOutputStream(output), manifest);

		String name = source.getPath().replace("\\", "/") + "/";
		for (File nestedFile: source.listFiles()){
			add(name, nestedFile, target);
		}
		target.close();
	}

	private void add(String base, File source, JarOutputStream target) throws IOException
	{
		BufferedInputStream in = null;
		try
		{
			String name = source.getPath().replace("\\", "/").replaceAll(base, "");
			if (source.isDirectory())
			{

				if (!name.isEmpty())
				{
					if (!name.endsWith("/")){
						name += "/";
					}

					JarEntry entry = new JarEntry(name);
					entry.setTime(source.lastModified());
					target.putNextEntry(entry);
					target.closeEntry();
				}
				for (File nestedFile: source.listFiles()){
					add(base, nestedFile, target);
				}
				return;
			} else if (! (source.getName().endsWith(".class") || source.getName().endsWith(".properties"))){
				return;
			}

			JarEntry entry = new JarEntry(name);
			entry.setTime(source.lastModified());
			target.putNextEntry(entry);
			in = new BufferedInputStream(new FileInputStream(source));


			byte[] buffer = new byte[1024];	
			while (true)
			{
				int count = in.read(buffer);
				if (count == -1){
					break;
				}
				target.write(buffer, 0, count);
			}
			target.closeEntry();
		}
		finally
		{
			if (in != null)
				in.close();
		}
	}

	private StringBuilder writeModule(ModuleNode module, Set<QualifiedNameNode> packages) {
		StringBuilder builder = new StringBuilder("import lense.core.lang.reflection.Module;\n")
				.append(" import lense.core.lang.String;\n import lense.core.lang.Boolean;\n import lense.core.math.Integer;\n")
				.append(" import lense.core.lang.Version;\n import lense.core.collections.Sequence; import lense.core.collections.LinkedList;\n")
				.append(" import lense.core.lang.reflection.Package;\n import lense.core.lang.Any;\n");

		int i=1;
		for(QualifiedNameNode pack : packages){ 
			builder.append(" import ").append(pack).append(".Package$$Info as Pack").append(i).append(";\n");
			i++;
		}

		builder.append("public class ").append("Module$$Info implements Module {\n");

		builder.append("public function getName() : String{\n");
		builder.append("	return \"").append(module.getName()).append("\";\n");
		builder.append("}\n");
		
		builder.append("public function getVersion(): Version{\n");
		builder.append("	return new Version(\"").append(module.getVersion()).append("\");\n");
		builder.append("}");
		builder.append("public function getPackages() : Sequence<Package> {\n");
		builder.append("	var  all : LinkedList<Package>= new LinkedList<Package>();\n ");

		for(i =0; i < packages.size(); i++){ 
			builder.append("	all.add(new Pack").append(i+1).append("());\n");
		}

		builder.append(" 	return all;");
		builder.append("}\n")
		.append(" public function equalsTo( other : Any) : Boolean {\n")
		.append("	return false;\n")
		.append("}\n")
		.append(" public function hashValue() : Integer {\n")
		.append("	return getName().hashValue();\n")
		.append("}\n");
		builder.append("}");
		return builder;
	}

	private StringBuilder writePackage(QualifiedNameNode pack) {
		StringBuilder builder = new StringBuilder("import lense.core.lang.reflection.Package; import lense.core.lang.Any; import lense.core.lang.String; import lense.core.lang.Boolean; import lense.core.math.Integer;")
				.append("public class Package$$Info implements Package { \n")
				.append(" public constructor ();")
				.append(" public function getName() : String {\n")
				.append("	return \"").append(pack).append("\" ;\n")
				.append("}\n")
				.append(" public function equalsTo( other: Any) : Boolean {\n")
				.append("	return false;\n")
				.append("}\n")
				.append(" public function hashValue() : Integer {\n")
				.append("	return getName().hashValue();\n")
				.append("}\n")
				.append("}\n");
		return builder;
	}

	private void compileNative(FileLocations fileLocations, Map<String, File> nativeTypes) throws IOException {

		if (!fileLocations.getNativeFolder().exists()){
			return;
		}
		List<File> files = new LinkedList<>();


		final Path rootDir = fileLocations.getNativeFolder().toPath();


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


		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

		Iterable<? extends JavaFileObject> compilationUnits1 = fileManager.getJavaFileObjectsFromFiles(files);
		if (compiler.getTask(new PrintWriter(System.err), fileManager, null, null, null, compilationUnits1).call()){

			for (File n : files){
				String packageFile = n.getAbsolutePath().substring(rootDir.toString().length());
				int pos = packageFile.indexOf(".java");
				packageFile = packageFile.substring(0, pos) + ".class";
				File source = new File(n.getParentFile(), n.getName().substring(0,  n.getName().length() - 5) + ".class");


				File target = new File(fileLocations.getTargetFolder(),packageFile);

				target.getParentFile().mkdirs();

				if (!source.exists()){
					System.err.println("Compiled file with java compiler does not exist");
				} else {
					Files.move(source.toPath(), target.toPath());
					nativeTypes.put(packageFile.substring(1).replace(File.separatorChar, '.').replaceAll(".class",""), target);
				}

			}
		} else {
			System.err.println("Cannot compile source with java compiler");
		}

	}


}
