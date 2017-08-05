/**
 * 
 */
package lense.compiler.ast;

/**
 * 
 */
public class LiteralIntervalNode extends ExpressionNode {

	private boolean startClosed;
	private boolean endClosed;
	
	private boolean startInf;
	private boolean endInf;
	
	/**
	 * @return
	 */
	public ExpressionNode getStart() {
		return (ExpressionNode)this.getChildren().get(0);
	}
	
	public ExpressionNode getEnd() {
		return (ExpressionNode)this.getChildren().get(1);
	}

	public boolean isEndClosed() {
		return endClosed;
	}

	public void setEndClosed(boolean endClosed) {
		this.endClosed = endClosed;
	}

	public boolean isStartClosed() {
		return startClosed;
	}

	public void setStartClosed(boolean startClosed) {
		this.startClosed = startClosed;
	}

	public boolean isEndInf() {
		return endInf;
	}

	public void setEndInf(boolean endInf) {
		this.endInf = endInf;
		if (endInf){
			this.endClosed = false;
		}
	}

	public boolean isStartInf() {
		return startInf;
	}

	public void setStartInf(boolean startInf) {
		this.startInf = startInf;
		if (startInf){
			this.startClosed = false;
		}
	}


}
