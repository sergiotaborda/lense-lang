package lense.compiler.crosscompile.java;

import compiler.CompilerBackEnd;
import lense.compiler.CompilerBackEndFactory;
import lense.compiler.FileLocations;

public class JavaSourceBackEnd implements CompilerBackEndFactory {

	@Override
	public CompilerBackEnd create(FileLocations target) {
		 return new OutToJavaSource(target);
	}

}
