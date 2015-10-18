/**
 * 
 */
package lense.compiler.crosscompile.java.ast;




/**
 * 
 */
public class TryStatement extends JavaAstNode {

	private BlockNode instructions;
	private ExpressionNode resource;
	private BlockNode finalInstructions;
	private CatchOptionsNode catchOptions;

	public BlockNode getInstructions(){
		return instructions;
	}
	
	public ExpressionNode getResource(){
		return resource;
	}
	
	public BlockNode getfinalInstructions(){
		return finalInstructions;
	}
	
	public CatchOptionsNode getCatchOptions(){
		return catchOptions;
	}
	
	/**
	 * @param blockNode
	 */
	public void setInstructions(BlockNode blockNode) {
		this.instructions = blockNode;
		this.add(blockNode);
	}

	/**
	 * @param expressionNode
	 */
	public void setResource(ExpressionNode expressionNode) {
		this.resource = expressionNode;
		this.add(expressionNode);
	}

	/**
	 * @param blockNode
	 */
	public void setFinally(BlockNode blockNode) {
		this.finalInstructions = blockNode;
		this.add(finalInstructions);
	}

	/**
	 * @param catchOptionsNode
	 */
	public void setCatchOptions(CatchOptionsNode catchOptionsNode) {
		this.catchOptions = catchOptionsNode;
		this.add(catchOptionsNode);
	}

}
