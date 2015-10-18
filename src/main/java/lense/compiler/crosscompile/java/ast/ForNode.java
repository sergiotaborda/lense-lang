/**
 * 
 */
package lense.compiler.crosscompile.java.ast;




/**
 * 
 */
public class ForNode extends StatementNode {

	private VariableDeclarationNode variableDeclarationNode;
	private ExpressionNode conditional;
	private ExpressionNode increment;
	private BlockNode blockNode;

	/**
	 * @param variableDeclarationNode
	 */
	public void setVariableDeclarationNode(
			VariableDeclarationNode variableDeclarationNode) {
		this.variableDeclarationNode = variableDeclarationNode;
		this.add(variableDeclarationNode);
	}

	/**
	 * @param expressionNode
	 */
	public void setConditional(ExpressionNode conditional) {
		this.conditional = conditional;
		this.add(conditional);
	}
	
	public void setIncrement(ExpressionNode increment) {
		this.increment = increment;
		this.add(increment);
	}

	/**
	 * @param blockNode
	 */
	public void setBlock(BlockNode blockNode) {
		this.blockNode = blockNode; 
		this.add(blockNode);
	}

	/**
	 * Obtains {@link VariableDeclarationNode}.
	 * @return the variableDeclarationNode
	 */
	public VariableDeclarationNode getVariableDeclarationNode() {
		return variableDeclarationNode;
	}

	/**
	 * Obtains {@link ExpressionNode}.
	 * @return the container
	 */
	public ExpressionNode getConditional() {
		return conditional;
	}
	
	public ExpressionNode getIncrement() {
		return increment;
	}


	/**
	 * Obtains {@link BlockNode}.
	 * @return the blockNode
	 */
	public BlockNode getStatements() {
		return blockNode;
	}

	
}
