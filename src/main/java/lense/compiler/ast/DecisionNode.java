/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.BlockNode;
import lense.compiler.ast.ConditionalStatement;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.StatementNode;
import compiler.syntax.AstNode;


/**
 * 
 */
public class DecisionNode extends StatementNode implements ConditionalStatement {

	private ExpressionNode condition;
	private BlockNode trueBlock;
	private AstNode falseBlock;

	/**
	 * @param expressionNode
	 */
	public void setCondition(ExpressionNode condition) {
		this.condition = condition;
		this.add(condition);
	}

	/**
	 * @param blockNode
	 */
	public void setTruePath(BlockNode trueBlock) {
		this.trueBlock = trueBlock;
		this.add(trueBlock);
	}

	/**
	 * @param blockNode
	 */
	public void setFalsePath(AstNode falseBlock) {
		this.falseBlock = falseBlock;
		this.add(falseBlock);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExpressionNode getCondition() {
		return condition;
	}

	/**
	 * Obtains {@link BlockNode}.
	 * @return the trueBlock
	 */
	public BlockNode getTrueBlock() {
		return trueBlock;
	}

	/**
	 * Obtains {@link AstNode}.
	 * @return the falseBlock
	 */
	public AstNode getFalseBlock() {
		return falseBlock;
	}
	
	
}
