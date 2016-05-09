/**
 * 
 */
package lense.compiler.ast;

/**
 * 
 */
public abstract class LiteralExpressionNode extends ExpressionNode implements Literal{

	/**
	 * @return
	 */
	public abstract String getLiteralValue();

}
