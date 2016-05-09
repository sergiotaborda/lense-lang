/**
 * 
 */
package lense.compiler.ast;

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
	
	@Override
	public  void replace(AstNode node, AstNode newnode){
		
		if (node == access){
			access = newnode;
		}
		
		if (node == indexExpression){
			indexExpression = (ExpressionNode) newnode;
		}
		
		super.replace(node, newnode);
	}
}
