/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.ExpressionNode;




/**
 * 
 */
public class IntervalNode extends ExpressionNode {

	private IntervalOperation operation;

	/**
	 * @return
	 */
	public ExpressionNode getStart() {
		return (ExpressionNode)this.getChildren().get(0);
	}
	
	public ExpressionNode getEnd() {
		return (ExpressionNode)this.getChildren().get(1);
	}

	public void setIntervalOperation(IntervalOperation operation) {
		this.operation = operation;	
	}

	public IntervalOperation getIntervalOperation() {
		return this.operation;	
	}
}
