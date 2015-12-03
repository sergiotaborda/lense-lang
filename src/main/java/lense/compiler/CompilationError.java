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
	
	/**
	 * Constructor.
	 * @param string
	 */
	public CompilationError(ScanPosition position, String msg) {
		super(msg(position,msg));
	}
	
	private static String msg(ScanPosition position, String msg){
		if (position == null){
			return  msg;
		}
		return "In compilation unit " +  position.getCompilationUnit().getName() + " error at " + position.getLineNumber() +"," +  position.getColumnNumber() + ":" + msg;
	}

}
