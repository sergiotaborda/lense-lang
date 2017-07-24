/**
 * 
 */
package lense.compiler.ast;

/**
 * 
 */
public class RangeNode extends ExpressionNode {

	/**
	 * @return
	 */
	public ExpressionNode getStart() {
		return (ExpressionNode)this.getChildren().get(0);
	}
	
	public ExpressionNode getEnd() {
		return (ExpressionNode)this.getChildren().get(1);
	}


}
