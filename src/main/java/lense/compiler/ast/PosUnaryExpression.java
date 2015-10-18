/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.BooleanOperatorNode.BooleanOperation;
import lense.compiler.ast.ExpressionNode;
import compiler.typesystem.TypeDefinition;

/**
 * 
 */
public class PosUnaryExpression extends ExpressionNode {

	private BooleanOperation operation;

	/**
	 * Constructor.
	 * @param resolveBooleanOperation
	 */
	public PosUnaryExpression(BooleanOperation operation) {
		this.operation = operation;
	}
	
	public TypeDefinition getTypeDefinition(){
		return ((ExpressionNode)this.getChildren().get(0)).getTypeDefinition();
	}
	
	public BooleanOperation getOperation(){
		return operation;
	}

}
