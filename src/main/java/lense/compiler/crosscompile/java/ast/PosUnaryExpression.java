/**
 * 
 */
package lense.compiler.crosscompile.java.ast;

import lense.compiler.ast.BooleanOperatorNode.BooleanOperation;

import compiler.typesystem.TypeDefinition;

/**
 * 
 */
public class PosUnaryExpression extends ExpressionNode {

	private lense.compiler.ast.BooleanOperatorNode.BooleanOperation operation;

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
	
	public void setOperation(BooleanOperation op){
		this.operation  = op;
	}

}
