package lense.compiler.crosscompile.java;

import compiler.CompilerBackEnd;
import compiler.filesystem.SourceFolder;
import lense.compiler.CompilerBackEndFactory;
import lense.compiler.FileLocations;

public class JavaSourceBackEnd implements CompilerBackEndFactory {

	@Override
	public CompilerBackEnd create(FileLocations target) {
		 return new OutToJavaSource(target);
	}

	@Override
	public void setClasspath(SourceFolder base) {
		// no-op
	}



}
