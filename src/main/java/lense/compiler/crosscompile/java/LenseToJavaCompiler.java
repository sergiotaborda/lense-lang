/**
 * 
 */
package lense.compiler.crosscompile.java;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import lense.compiler.CompilationError;
import lense.compiler.FileLocations;
import lense.compiler.LenseCompiler;
import lense.compiler.ast.ModuleNode;
import lense.compiler.crosscompile.ErasurePhase;
import lense.compiler.modules.ModulesRepository;
import lense.compiler.phases.CompositePhase;
import lense.compiler.phases.DesugarPhase;
import lense.compiler.repository.TypeRepository;

/**
 * 
 */
public class LenseToJavaCompiler extends LenseCompiler{



    private JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

	public LenseToJavaCompiler (ModulesRepository globalModulesRepository){
		// The global, even remote, repository
		super("java",globalModulesRepository,new  JavaCompilerBackEndFactory()); // JavaBackEndFactory(); //new  JavaSourceBackEnd(); //JavaBinaryBackEndFactory();
	}

	protected void initCorePhase(CompositePhase corePhase, Map<String, File> nativeTypes){
	    
	    DesugarPhase desugarProperties = new DesugarPhase(this.getCompilerListener());
	    ErasurePhase erasurePhase = new ErasurePhase(this.getCompilerListener());
       // IntermediatyRepresentationPhase  ir = new IntermediatyRepresentationPhase();
        JavalizePhase  jv = new JavalizePhase(this.getCompilerListener(),nativeTypes);
        
        corePhase.add(desugarProperties).add(erasurePhase).add(jv);//.add(ir);
  
	}

    protected void createModuleArchive(FileLocations locations, ModuleNode module, File base, Set<String> applications) throws IOException, FileNotFoundException {
        StringBuilder builder;
        Manifest jarManifest = new Manifest();
        jarManifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        
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

            jarManifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, pack + ".Bootstrap");
        	
        }

        
        File file = new File (locations.getModulesFolder(), module.getName() + ".jar");
        
        Properties p = new Properties();

        p.put("module.name", module.getName());
        p.put("module.version", module.getVersion().toString());

        File moduleProperties = new File(locations.getTargetFolder(), "module.properties");
        p.store(new FileOutputStream(moduleProperties), "Lense module definition");

        createJar(p,locations.getTargetFolder(), file,jarManifest);
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
			addToJar(name, nestedFile, target);
		}
		target.close();
	}

	private void addToJar(String base, File source, JarOutputStream target) throws IOException
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
					addToJar(base, nestedFile, target);
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

	protected void compileNative(FileLocations fileLocations, Map<String, File> nativeTypes) throws IOException {

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
