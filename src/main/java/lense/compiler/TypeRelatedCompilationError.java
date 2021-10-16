package lense.compiler;

import compiler.lexer.ScanPosition;
import compiler.lexer.ScanPositionHolder;

public abstract class TypeRelatedCompilationError extends CompilationError {

	private static final long serialVersionUID = 4012698791654898853L;

	public TypeRelatedCompilationError(ScanPosition position, String msg) {
		super(position, msg);
	}

	public TypeRelatedCompilationError(ScanPositionHolder holder, String msg) {
		super(holder, msg);
	}
	
	public abstract String getTypeName();
}
