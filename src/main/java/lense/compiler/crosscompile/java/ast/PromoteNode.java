/**
 * 
 */
package lense.compiler.crosscompile.java.ast;

import compiler.typesystem.TypeDefinition;


/**
 * 
 */
public class PromoteNode extends ExpressionNode {

	private ExpressionNode other;
	private TypeDefinition from;

	/**
	 * Constructor.
	 * @param type 
	 * @param inicializer
	 */
	public PromoteNode(ExpressionNode other, TypeDefinition from, TypeDefinition to) {
		this.other = other;
		this.add(other);
		this.type = to;
		this.from = from;
	}

}
