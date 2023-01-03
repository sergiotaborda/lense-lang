package lense.compiler;

import java.util.List;

import compiler.CompilerBackEnd;
import compiler.filesystem.SourceFolder;

public interface CompilerBackEndFactory {

	CompilerBackEnd create(FileLocations locations);

	void setClasspath(List<SourceFolder> classpath);

}
