/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public class PosExpression extends ExpressionNode{

	private ArithmeticOperation operation;

	/**
	 * Constructor.
	 * @param addition
	 */
	public PosExpression(ArithmeticOperation op) {
		this.operation = op;
	}

	public TypeVariable getTypeVariable(){
		return ((ExpressionNode)this.getChildren().get(0)).getTypeVariable();
	}
	
	public ArithmeticOperation getOperation(){
		return operation;
	}

}
