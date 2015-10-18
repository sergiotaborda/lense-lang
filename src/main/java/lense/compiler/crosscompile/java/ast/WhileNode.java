/**
 * 
 */
package lense.compiler.crosscompile.java.ast;




/**
 * 
 */
public class WhileNode extends StatementNode implements ConditionalStatement {

	private BlockNode block;
	private ExpressionNode conditional;

	/**
	 * @param astNode
	 */
	public void setBlock(BlockNode block) {
		this.block = block;
		this.add(block);
	}

	/**
	 * @param blockNode
	 */
	public void setCondition(ExpressionNode exp) {
		conditional = exp;
		this.add(exp);
	}

	/**
	 * Obtains {@link BlockNode}.
	 * @return the block
	 */
	public BlockNode getStatements() {
		return block;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExpressionNode getCondition() {
		return conditional;
	}

}
