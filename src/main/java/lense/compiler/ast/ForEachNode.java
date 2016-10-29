/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.BlockNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.StatementNode;
import lense.compiler.ast.VariableDeclarationNode;
import lense.compiler.context.SemanticScope;
import lense.compiler.phases.ScopeDelimiter;
import compiler.syntax.AstNode;



/**
 * 
 */
public class ForEachNode extends StatementNode implements ScopeDelimiter{

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
	public BlockNode getBlock() {
		return blockNode;
	}

	public void replace(AstNode node, AstNode newnode){
		super.replace(node, newnode);
		
		if (this.variableDeclarationNode == node){
			this.variableDeclarationNode = (VariableDeclarationNode) newnode;
		} else if (this.blockNode == node){
			this.blockNode = (BlockNode) newnode;
		} else if (this.container == node){
			this.container = (ExpressionNode) newnode;
		}
	}

	@Override
	public String getScopeName() {
		return "for";
	}

}
