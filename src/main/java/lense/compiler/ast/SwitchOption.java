/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.BlockNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.LenseAstNode;
import compiler.syntax.AstNode;

/**
 * 
 */
public class SwitchOption extends StatementNode {

	private ExpressionNode value;
	private BlockNode actions;
	private boolean isDefault;

	public SwitchOption(){
		this(false);
	}
	
	public SwitchOption(boolean isDefault){
		this.isDefault = isDefault;
	}
	/**
	 * @param expressionNode
	 */
	public void setValue(ExpressionNode expressionNode) {
		this.value = expressionNode;
		this.add(expressionNode);
	}

	/**
	 * @param astNode
	 */
	public void setActions(BlockNode node) {
		this.actions = node;
		this.add(node);
	}

	/**
	 * Obtains {@link ExpressionNode}.
	 * @return the value
	 */
	public ExpressionNode getValue() {
		return value;
	}

	/**
	 * Obtains {@link AstNode}.
	 * @return the actions
	 */
	public BlockNode getActions() {
		return actions;
	}

	/**
	 * @return
	 */
	public boolean isDefault() {
		return isDefault;
	}

}
