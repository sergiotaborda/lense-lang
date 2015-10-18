/**
 * 
 */
package lense.compiler.crosscompile.java.ast;




/**
 * 
 */
public class ForEachNode extends StatementNode {

	private VariableDeclarationNode variableDeclarationNode;
	private ExpressionNode container;
	private BlockNode blockNode;

	/**
	 * @param variableDeclarationNode
	 */
	public void setIterableVariable(
			VariableDeclarationNode variableDeclarationNode) {
		this.variableDeclarationNode = variableDeclarationNode;
		this.add(variableDeclarationNode);
	}

	/**
	 * @param expressionNode
	 */
	public void setContainer(ExpressionNode expressionNode) {
		this.container = expressionNode;
		this.add(container);
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
	public ExpressionNode getContainer() {
		return container;
	}

	/**
	 * Obtains {@link BlockNode}.
	 * @return the blockNode
	 */
	public BlockNode getStatements() {
		return blockNode;
	}

	
}
