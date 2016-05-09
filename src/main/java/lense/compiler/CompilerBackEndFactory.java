package lense.compiler;

import compiler.CompilerBackEnd;

public interface CompilerBackEndFactory {

	CompilerBackEnd create(FileLocations locations);

}
