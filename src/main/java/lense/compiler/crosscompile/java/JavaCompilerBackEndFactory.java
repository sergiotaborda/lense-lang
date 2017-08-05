package lense.compiler.crosscompile.java;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import compiler.CompiledUnit;
import compiler.CompilerBackEnd;
import lense.compiler.CompilerBackEndFactory;
import lense.compiler.FileLocations;

public class JavaCompilerBackEndFactory implements CompilerBackEndFactory {

	
	JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
	private File base;

	public JavaCompilerBackEndFactory (){}
	
	@Override
	public CompilerBackEnd create(FileLocations fileLocations) {
		return new JavaCompilerBackEnd(fileLocations);
	}

	public class JavaCompilerBackEnd implements CompilerBackEnd {

		private FileLocations fileLocations;
		private OutToJavaSource source;

		public JavaCompilerBackEnd(FileLocations fileLocations) {
			this.fileLocations= fileLocations;
			this.source = new OutToJavaSource(fileLocations);
		}

		@Override
		public void use(CompiledUnit unit) {

			File file = source.toSource(unit);

			if (file == null){
				return;
			}
			
			try{

				Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(file));
				
				List<File> classPath = new ArrayList<>(2);
				if (base != null){
					for (File jar : base.listFiles(f -> f.getName().endsWith(".jar"))){
						classPath.add(jar);
					}
				}
				classPath.add(fileLocations.getTargetFolder());
				fileManager.setLocation(StandardLocation.CLASS_PATH, classPath);
			
				compiler.getTask(new PrintWriter(System.err), fileManager, null, null /*Arrays.asList("-verbose")*/, null, compilationUnits).call();

				//file.delete();
			} catch (Exception e){
				throw new RuntimeException("Error compiling unit " + file.toString(),e);
			}
		}

	}

	@Override
	public void setClasspath(File base) {
		this.base = base;
	}

}
