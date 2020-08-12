package lense.compiler.crosscompile.java;

import compiler.CompiledUnit;
import compiler.CompilerBackEnd;
import compiler.filesystem.SourceFolder;
import lense.compiler.CompilerBackEndFactory;
import lense.compiler.FileLocations;

public class JavaBackEndFactory implements CompilerBackEndFactory {

	@Override
	public CompilerBackEnd create(FileLocations locations) {
		 return new CompositeCompilerBackEnd( new OutToJavaClass(locations), new OutToJavaSource(locations));
	}

	
	public static class CompositeCompilerBackEnd implements CompilerBackEnd {

		private OutToJavaSource outToJavaSource;
		private OutToJavaClass outToJavaClass;

		public CompositeCompilerBackEnd(OutToJavaClass outToJavaClass, OutToJavaSource outToJavaSource) {
			this.outToJavaClass = outToJavaClass;
			this.outToJavaSource = outToJavaSource;
		}

		@Override
		public void use(CompiledUnit unit) {
			outToJavaSource.use(unit);
			outToJavaClass.use(unit);
		}
		
	}


	@Override
	public void setClasspath(SourceFolder base) {
		// TODO Auto-generated method stub
		
	}

}
