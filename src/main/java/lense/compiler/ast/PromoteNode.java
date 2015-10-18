/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.ExpressionNode;
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
		this.setTypeDefinition(to);
		this.from = from;
	}

}
