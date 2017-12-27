/**
 * 
 */
package lense.compiler.ast;

/**
 * 
 */
public class RangeNode extends ExpressionNode {

	
	private boolean includeEnd = true;
	
	/**
	 * @return
	 */
	public ExpressionNode getStart() {
		return (ExpressionNode)this.getChildren().get(0);
	}
	
	public ExpressionNode getEnd() {
		return (ExpressionNode)this.getChildren().get(1);
	}

	
	public boolean isIncludeEnd() {
		return includeEnd;
	}

	public void setIncludeEnd(boolean includeEnd) {
		this.includeEnd = includeEnd;
	}


}
