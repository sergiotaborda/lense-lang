package lense.compiler.crosscompile.java;

import java.io.File;

import compiler.CompilerBackEnd;
import lense.compiler.CompilerBackEndFactory;
import lense.compiler.FileLocations;

public class JavaBinaryBackEndFactory implements CompilerBackEndFactory {

	@Override
	public CompilerBackEnd create(FileLocations target) {
		 return new OutToJavaClass(target);
	}

	@Override
	public void setClasspath(File base) {

	}



}
