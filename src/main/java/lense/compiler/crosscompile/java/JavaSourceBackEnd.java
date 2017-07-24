package lense.compiler.crosscompile.java;

import java.io.File;

import compiler.CompilerBackEnd;
import lense.compiler.CompilerBackEndFactory;
import lense.compiler.FileLocations;

public class JavaSourceBackEnd implements CompilerBackEndFactory {

	@Override
	public CompilerBackEnd create(FileLocations target) {
		 return new OutToJavaSource(target);
	}

	@Override
	public void setClasspath(File base) {
		// no-op
	}



}
