package lense.compiler.crosscompile.java;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import compiler.CompiledUnit;
import compiler.CompilerBackEnd;
import lense.compiler.CompilerBackEndFactory;
import lense.compiler.FileLocations;

public class JavaCompilerBackEndFactory implements CompilerBackEndFactory {

	
	JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

	public JavaCompilerBackEndFactory (){
		
	}
	
	@Override
	public CompilerBackEnd create(FileLocations target) {
		return new JavaCompilerBackEnd(target);
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

				Iterable<? extends JavaFileObject> compilationUnits1 = fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(file));
				compiler.getTask(new PrintWriter(System.err), fileManager, null, Arrays.asList("-classpath", fileLocations.getTargetFolder().getAbsolutePath()), null, compilationUnits1).call();

				//file.delete();
			} catch (Exception e){
				throw new RuntimeException("Error compiling unit " + file.toString(),e);
			}
		}

	}

}
