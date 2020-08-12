package lense.compiler;

import compiler.CompilerBackEnd;
import compiler.filesystem.SourceFolder;

public interface CompilerBackEndFactory {

	CompilerBackEnd create(FileLocations locations);

	void setClasspath(SourceFolder base);

}
