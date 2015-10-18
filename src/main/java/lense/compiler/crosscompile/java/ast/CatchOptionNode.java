/**
 * 
 */
package lense.compiler.crosscompile.java.ast;




/**
 * 
 */
public class CatchOptionNode extends JavaAstNode {

	private VariableDeclarationNode exceptions;
	private BlockNode instructions;

	/**
	 * @param astNode
	 */
	public void setExceptions(VariableDeclarationNode node) {
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
	public VariableDeclarationNode getExceptions() {
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
