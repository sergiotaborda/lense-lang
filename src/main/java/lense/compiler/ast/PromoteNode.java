/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.type.variable.TypeVariable;


/**
 * 
 */
public class PromoteNode extends ExpressionNode {

	private ExpressionNode other;
	private TypeVariable from;

	/**
	 * Constructor.
	 * @param type 
	 * @param inicializer
	 */
	public PromoteNode(ExpressionNode other, TypeVariable from, TypeVariable to) {
		this.other = other;
		this.add(other);
		this.setTypeVariable(to);
		this.from = from;
	}

}
