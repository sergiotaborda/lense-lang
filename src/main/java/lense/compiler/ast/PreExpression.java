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
public class PreExpression extends ExpressionNode{

	private ArithmeticOperation operation;

	/**
	 * Constructor.
	 * @param addition
	 */
	public PreExpression(ArithmeticOperation op) {
		this.operation = op;
	}

	public TypeDefinition getTypeDefinition(){
		return ((ExpressionNode)this.getChildren().get(0)).getTypeDefinition();
	}
	
	public ArithmeticOperation getOperation(){
		return operation;
	}

}
