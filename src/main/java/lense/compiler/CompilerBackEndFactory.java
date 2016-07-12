package lense.compiler;

import java.io.File;

import compiler.CompilerBackEnd;

public interface CompilerBackEndFactory {

	CompilerBackEnd create(FileLocations locations);

	void setClasspath(File base);

}
