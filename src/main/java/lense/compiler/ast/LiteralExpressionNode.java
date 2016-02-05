/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.ExpressionNode;


/**
 * 
 */
public abstract class LiteralExpressionNode extends ExpressionNode implements Literal{

	/**
	 * @return
	 */
	public abstract String getLiteralValue();

}
