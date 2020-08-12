package lense.compiler.crosscompile.java;

import compiler.CompilerBackEnd;
import compiler.filesystem.SourceFolder;
import lense.compiler.CompilerBackEndFactory;
import lense.compiler.FileLocations;

public class JavaBinaryBackEndFactory implements CompilerBackEndFactory {

	@Override
	public CompilerBackEnd create(FileLocations target) {
		 return new OutToJavaClass(target);
	}

	@Override
	public void setClasspath(SourceFolder base) {

	}



}
