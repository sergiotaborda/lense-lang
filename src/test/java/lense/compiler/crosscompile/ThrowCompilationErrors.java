package lense.compiler.crosscompile;

import compiler.CompilerListener;
import compiler.CompilerMessage;
import lense.compiler.CompilationError;

public class ThrowCompilationErrors implements CompilerListener{

	@Override
	public void start() {
		///no-op
	}

	@Override
	public void error(CompilerMessage message) {
		throw new CompilationError( message.getPosition(), message.getMessage());
	}

	@Override
	public void warn(CompilerMessage message) {
		///no-op
	}

	@Override
	public void trace(CompilerMessage message) {
		///no-op
	}

	@Override
	public void end() {
		///no-op
	}

}
