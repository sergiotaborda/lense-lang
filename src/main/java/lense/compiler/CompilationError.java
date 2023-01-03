/**
 * 
 */
package lense.compiler;

import compiler.lexer.ScanPosition;
import compiler.lexer.ScanPositionHolder;

/**
 * 
 */
public class CompilationError extends compiler.CompilationError {


	private static final long serialVersionUID = -5058350526414899602L;

	public CompilationError( String msg) {
		super( msg);
	}
	
	public CompilationError(ScanPositionHolder holder, String msg) {
		this(holder == null ? null : holder.getScanPosition(),  msg);
	}
	

	public CompilationError(ScanPosition position, String msg) {
		super(position,msg);
	}


}
