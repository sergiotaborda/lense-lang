/**
 * 
 */
package lense.compiler.crosscompile.java.ast;

import compiler.typesystem.TypeDefinition;


/**
 * 
 */
public class TernaryConditionalExpressionNode extends ExpressionNode implements ConditionalStatement{
	
	ExpressionNode thenExpression;
	ExpressionNode elseExpression;
	private ExpressionNode conditional;
	
	
	public ExpressionNode getThenExpression(){
		return thenExpression;
	}
	
	public ExpressionNode getElseExpression(){
		return elseExpression;
	}
	
	/**
	 * @param astNode
	 */
	public void setCondition(ExpressionNode conditional) {
		this.conditional = conditional;
		this.add(conditional);
	}

	/**
	 * @param expressionNode
	 */
	public void setThenExpression(ExpressionNode exp) {
		thenExpression= exp;
		this.add(exp);
	}

	/**
	 * @param expressionNode
	 */
	public void setElseExpression(ExpressionNode exp) {
		elseExpression = exp;
		this.add(exp);
	}

	public TypeDefinition getTypeDefinition() {
		return null; //SenseTypeSystem.getInstance().unionOf( thenExpression.getTypeDefinition(), elseExpression.getTypeDefinition());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExpressionNode getCondition() {
		return conditional;
	}
}
