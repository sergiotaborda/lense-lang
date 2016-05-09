
/**
 * 
 */
package lense.compiler.ast;


import lense.compiler.type.variable.TypeVariable;


/**
 * 
 */
public abstract class ExpressionNode extends LenseAstNode implements TypedNode {

	private TypeVariable type;

	public TypeVariable getTypeVariable() {
		return type;
	}

	public void setTypeVariable(TypeVariable type) {
		this.type = type;
	}
}
