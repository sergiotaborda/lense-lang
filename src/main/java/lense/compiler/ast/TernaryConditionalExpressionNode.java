/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.ast.ConditionalStatement;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.type.TypeDefinition;


/**
 * 
 */
public class TernaryConditionalExpressionNode extends ExpressionNode implements ConditionalStatement{
	
	private ExpressionNode thenExpression;
	private ExpressionNode elseExpression;
	private ExpressionNode conditional;
	
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


	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExpressionNode getCondition() {
		return conditional;
	}

	public ExpressionNode getElseExpression() {
		return elseExpression;
	}


	public ExpressionNode getThenExpression() {
		return thenExpression;
	}

}
