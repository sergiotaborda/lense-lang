/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.BlockNode;
import lense.compiler.ast.CatchOptionsNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.LenseAstNode;



/**
 * 
 */
public class TryStatement extends LenseAstNode {

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
