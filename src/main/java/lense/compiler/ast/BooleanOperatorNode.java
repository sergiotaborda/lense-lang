/**
 * 
 */
package lense.compiler.ast;

/**
 * 
 */
public class BooleanOperatorNode extends BooleanExpressionNode {
	
	private BooleanOperation operation;

	/**
	 * Constructor.
	 * @param resolveBooleanOperation
	 */
	public BooleanOperatorNode(BooleanOperation operation) {
		this.operation = operation;
	}
	
	public BooleanOperatorNode(BooleanOperation operation, ExpressionNode left, ExpressionNode right) {
		this.operation = operation;
		this.add(left);
		this.add(right);
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
	public BooleanOperation getOperation() {
		return operation;
	}

}
