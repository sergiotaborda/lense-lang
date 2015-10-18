/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.ArithmeticOperation;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.NeedTypeCalculationNode;





/**
 * 
 */
public class ArithmeticNode extends NeedTypeCalculationNode {

	private ArithmeticOperation operation;

	/**
	 * Constructor.
	 * @param resolveOperation
	 */
	public ArithmeticNode(ArithmeticOperation operation) {
		this.operation = operation;
	}

	
	public ExpressionNode getLeft(){
		return (ExpressionNode) this.getChildren().get(0);
	}
	
	public ExpressionNode getRight(){
		return (ExpressionNode) this.getChildren().get(1);
	}


	/**
	 * @return
	 */
	public ArithmeticOperation getOperation() {
		return operation;
	}


	public void setOperation(ArithmeticOperation operation) {
		this.operation = operation;
	}

}
