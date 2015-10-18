/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.ArithmeticOperation;
import lense.compiler.ast.ExpressionNode;
import compiler.typesystem.TypeDefinition;

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

	public TypeDefinition getTypeDefinition(){
		return ((ExpressionNode)this.getChildren().get(0)).getTypeDefinition();
	}
	
	public ArithmeticOperation getOperation(){
		return operation;
	}

}
