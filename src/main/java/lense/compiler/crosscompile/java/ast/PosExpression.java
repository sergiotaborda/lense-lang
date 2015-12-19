/**
 * 
 */
package lense.compiler.crosscompile.java.ast;

import compiler.typesystem.TypeDefinition;

/**
 * 
 */
public class PosExpression extends ExpressionNode{

	private ArithmeticOperation operation;

	public PosExpression() {
		this(ArithmeticOperation.Increment);
	}
	
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
	
	public void setOperation(ArithmeticOperation other){
		this.operation = other;
	}

	/**
	 * @param returningType
	 */
	public void setTypeDefinition(TypeDefinition type) {
		this.type = type;
	}
}
