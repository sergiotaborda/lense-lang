/**
 * 
 */
package lense.compiler.crosscompile.java;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import compiler.CompilerListener;
import compiler.filesystem.SourceFile;
import compiler.filesystem.SourceFileSystemNode;
import compiler.filesystem.SourceFolder;
import compiler.filesystem.SourceWalker;
import compiler.filesystem.SourceWalkerResult;
import lense.compiler.CompilationError;
import lense.compiler.FileLocations;
import lense.compiler.LenseCompiler;
import lense.compiler.PackageSourcePathUtils;
import lense.compiler.asm.ByteCodeTypeDefinitionReader;
import lense.compiler.ast.ModuleImportNode;
import lense.compiler.ast.ModuleNode;
import lense.compiler.crosscompile.ErasurePhase;
import lense.compiler.crosscompile.NativePeersPhase;
import lense.compiler.crosscompile.java.JavaCompilerBackEndFactory.JavaCompilerBackEnd;
import lense.compiler.modules.ModulesRepository;
import lense.compiler.phases.CompositePhase;
import lense.compiler.phases.DesugarPhase;
import lense.compiler.phases.EnhancementPhase;
import lense.compiler.phases.ReificationPhase;
import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.type.TypeDefinition;

/**
 * 
 */
public class LenseToJavaCompiler extends LenseCompiler{


	private final static JavaCompilerBackEndFactory javaCompilerBackEndFactory = new JavaCompilerBackEndFactory();

	public LenseToJavaCompiler (ModulesRepository globalModulesRepository){
		super("java",globalModulesRepository,javaCompilerBackEndFactory); 
	}

	protected void initCorePhase(CompositePhase corePhase, Map<String, SourceFile> nativeTypes, UpdatableTypeRepository typeContainer){

		CompilerListener compilerListener = this.getCompilerListener();
		corePhase
			.add(new DesugarPhase(compilerListener))
			.add(new EnhancementPhase(compilerListener))
			.add(new ReificationPhase(compilerListener))
			.add(new ErasurePhase(compilerListener))
			.add(new NativePeersPhase(compilerListener,nativeTypes))
			.add(new JavalizePhase(compilerListener,nativeTypes, typeContainer));
			//.add(ir);

	}

	protected void createModuleArchive(FileLocations locations, ModuleNode module,Set<String> applications) throws IOException, FileNotFoundException {
		StringBuilder builder;
		Manifest jarManifest = new Manifest();
		jarManifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

		JavaCompilerBackEnd compilerBackEnd = javaCompilerBackEndFactory.create(locations);

		// add bootstrap class
		if (!applications.isEmpty()){

			if (applications.size() > 1){
				throw new CompilationError("More than one Application was found. Every module can only have a maximum of 1 application");
			}
			String mainType = applications.iterator().next();

			int pos = mainType.lastIndexOf('.');
			String pack = mainType.substring(0, pos);

			builder = writeBootstrap(mainType, pack);

			var sourceFile = locations.getTargetFolder().folder(PackageSourcePathUtils.fromPackageName(pack)).file("Bootstrap.java");
			try(Writer writer = sourceFile.writer()){
				writer.write(builder.toString());
				writer.close();
			}

			compilerBackEnd.compile(sourceFile);

			//        	StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
			//        	
			//        	List<File> classPath = new ArrayList<>(2);
			//            if (base != null){
			//                for (File jar : base.listFiles(f -> f.getName().endsWith(".jar"))){
			//                    classPath.add(jar);
			//                }
			//            }
			//            classPath.add(locations.getTargetFolder());
			//            fileManager.setLocation(StandardLocation.CLASS_PATH, classPath);
			//            
			//        	Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(sourceFile));
			//
			//            compiler.getTask(new PrintWriter(System.err),fileManager, null, null, null,  compilationUnits).call();

			jarManifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, pack + ".Bootstrap");

		}


		var file = locations.getModulesFolder().file(module.getName() + ".jar");

		Properties p = new Properties();

		p.put("module.name", module.getName());
		p.put("module.version", module.getVersion().toString());
		
		var requirements = module.getImports().getChildren(ModuleImportNode.class);
				
		if(!requirements.isEmpty()) {
			var buffer= new StringBuffer();
			for(var m : requirements) {
				if(buffer.length() >0) {
					buffer.append(";");
				}
				buffer.append(m.getQualifiedNameNode().toString()).append("@").append(m.getVersionNode().getVersion().toString());
			}
			
			p.put("module.requires", buffer.toString());
		}
	
		
		var moduleProperties = locations.getTargetFolder().file("module.properties");
		p.store(moduleProperties.outputStream(), "Lense module definition");

		createJar(locations.getTargetFolder(), file,jarManifest);
	}

	private StringBuilder writeBootstrap(String applicationType , String pack) {


		StringBuilder builder = new StringBuilder("package ").append(pack).append(";\n")
				.append("import lense.core.system.ConsoleApplication;\n")
				.append("import lense.core.collections.Array;\n")
				.append("public class Bootstrap {\n")
				.append("public static void main(String[] args) {\n")
				.append("		ConsoleApplication app = ").append(applicationType).append(".constructor();\n")
				.append("		app.setArguments(Array.fromNative(lense.core.lang.reflection.JavaReifiedArguments.getInstance().addType(lense.core.lang.String.TYPE_RESOLVER) ,args, s -> lense.core.lang.String.valueOfNative(s)));\n")
				.append("		app.onStart();\n")
				.append("	}")
				.append("}");


		return builder;
	}

	private void createJar(SourceFolder source, SourceFile output, Manifest manifest) throws IOException {

		JarOutputStream target = new JarOutputStream(output.outputStream(), manifest);

		String name = source.getPath().join("/") + "/";
		for (var nestedFile: source.children()){
			addToJar(name, nestedFile, target);
		}
		target.close();
	}

	private void addToJar(String base, SourceFileSystemNode source, JarOutputStream target) throws IOException
	{
		BufferedInputStream in = null;
		try
		{
			String name = source.getPath().join("/").substring(base.length());
			if (source.isFolder())
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
				
				for (var nestedFile: source.asFolder().children()){
					addToJar(base, nestedFile, target);
				}
				
				return;
			} else if (! (source.getName().endsWith(".class") || source.getName().endsWith(".properties"))){
				return;
			}

			JarEntry entry = new JarEntry(name);
			entry.setTime(source.lastModified());
			target.putNextEntry(entry);
			in = new BufferedInputStream(source.asFile().inputStream());


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


	protected SourceFile resolveNativeFile(SourceFolder folder, String name) {
		return folder.file(name + ".class");
	}

	protected void collectNative(FileLocations fileLocations, Map<String, SourceFile> nativeTypes) throws IOException {

		if (!fileLocations.getNativeFolder().exists()){
			return;
		}

		List<SourceFile> files = new LinkedList<>();

		var rootDir = fileLocations.getNativeFolder();
	    
		rootDir.walkTree(new SourceWalker(){

			@Override
			public SourceWalkerResult visitFile(SourceFile file) {
				if (file.getName().endsWith(".java")){
					files.add(file);
				}
				return SourceWalkerResult.CONTINUE;
			}

		});

		if(files.isEmpty()) {
			return;
		}
		
		if (javaCompilerBackEndFactory.create(fileLocations).compile(files)){

			// compile all files
			for (var n : files){
				
				var name = n.getName().substring(0,  n.getName().length() - 5);
				var source = resolveNativeFile(n.parentFolder(), name);

				var packagePath = rootDir.getPath().relativize(n.getPath()).getParent();
				var packageFolder = fileLocations.getTargetFolder().folder(packagePath);
				
				var target =  packageFolder.file(name  + ".class");

				target.parentFolder().ensureExists();

				if (!source.exists()){
					throw new CompilationError("Compiled file with java compiler does not exist (" + source.toString() +"). ");
				} else {
					source.moveTo(target);
					
				
					nativeTypes.put(packagePath.join(".") + "." + name, target);

				}

			}


		} else {
			throw new CompilationError("Cannot compile source with java compile");
		}

	}
	
	@Override
	protected List<TypeDefinition> extactTypeDefinitionFronNativeType(
			UpdatableTypeRepository currentModuleRepository,
			Collection<SourceFile> nativeFiles
	) throws IOException {

	    var nativeTypesDefs = new LinkedList<TypeDefinition>();

  		var reader = new ByteCodeTypeDefinitionReader(currentModuleRepository);


		for( var target : nativeFiles) {
  			TypeDefinition type =  reader.readNative(target);
      		currentModuleRepository.registerType(type, type.getGenericParameters().size());

      		nativeTypesDefs.add(type);
  		}

		return nativeTypesDefs;
	}


}
