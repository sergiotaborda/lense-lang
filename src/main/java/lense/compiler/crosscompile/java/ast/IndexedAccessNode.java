/**
 * 
 */
package lense.compiler.crosscompile.java.ast;

import compiler.syntax.AstNode;


/**
 * 
 */
public class IndexedAccessNode extends NeedTypeCalculationNode{
	
	private ExpressionNode indexExpression;
	private AstNode access;

	/**
	 * @param methodCallNode
	 */
	public void setIndexExpression(ExpressionNode indexExpression) {
		this.indexExpression = indexExpression;
		this.add(indexExpression);
	}

	/**
	 * @param astNode
	 */
	public void setAccess(AstNode node) {
		this.access = node;
		this.add(node);
	}

	public ExpressionNode getIndexExpression(){
		return indexExpression;
	}
	
	/**
	 * Obtains {@link AstNode}.
	 * @return the access
	 */
	public AstNode getAccess() {
		return access;
	}
	
}
