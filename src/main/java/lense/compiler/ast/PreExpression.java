/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public class PreExpression extends ExpressionNode{

	private UnitaryOperation operation;

	/**
	 * Constructor.
	 * @param addition
	 */
	public PreExpression(UnitaryOperation op) {
		this.operation = op;
	}

	public TypeVariable getTypeVariable(){
		return ((ExpressionNode)this.getChildren().get(0)).getTypeVariable();
	}
	
	public UnitaryOperation getOperation(){
		return operation;
	}

}
