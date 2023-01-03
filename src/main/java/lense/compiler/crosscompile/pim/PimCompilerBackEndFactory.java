package lense.compiler.crosscompile.pim;

import java.util.List;

import compiler.CompilerBackEnd;
import compiler.filesystem.SourceFolder;
import lense.compiler.CompilerBackEndFactory;
import lense.compiler.FileLocations;

public class PimCompilerBackEndFactory implements CompilerBackEndFactory{

	@Override
	public CompilerBackEnd create(FileLocations locations) {
		return new PimCompilerBackEnd(locations);
	}

	@Override
	public void setClasspath(List<SourceFolder> classpath) {
		// no-op
		
	}

}
