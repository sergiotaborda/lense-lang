/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.BlockNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.LenseAstNode;
import lense.compiler.ast.VariableDeclarationNode;



/**
 * 
 */
public class CatchOptionNode extends LenseAstNode {

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
