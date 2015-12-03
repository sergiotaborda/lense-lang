/**
 * 
 */
package lense.compiler.ast;




/**
 * 
 */
public class CatchOptionNode extends LenseAstNode {

	private FormalParameterNode exceptions;
	private BlockNode instructions;

	/**
	 * @param astNode
	 */
	public void setExceptions(FormalParameterNode node) {
		this.exceptions = node;
		this.add(node);
	}

	/**
	 * @param astNode
	 */
	public void setInstructions(BlockNode node) {
		this.instructions = node;
		this.add(node);
	}

	/**
	 * Obtains {@link ExpressionNode}.
	 * @return the exceptions
	 */
	public FormalParameterNode getExceptions() {
		return exceptions;
	}

	/**
	 * Obtains {@link BlockNode}.
	 * @return the instructions
	 */
	public BlockNode getInstructions() {
		return instructions;
	}
	
	

}
