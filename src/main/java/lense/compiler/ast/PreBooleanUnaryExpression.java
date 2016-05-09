/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.BooleanOperatorNode.BooleanOperation;
import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public class PreBooleanUnaryExpression extends ExpressionNode {

	private BooleanOperation operation;

	/**
	 * Constructor.
	 * @param resolveBooleanOperation
	 */
	public PreBooleanUnaryExpression(BooleanOperation operation) {
		this.operation = operation;
	}
	
	public TypeVariable getTypeVariable(){
		return ((ExpressionNode)this.getChildren().get(0)).getTypeVariable();
	}
	
	public BooleanOperation getOperation(){
		return operation;
	}

}
