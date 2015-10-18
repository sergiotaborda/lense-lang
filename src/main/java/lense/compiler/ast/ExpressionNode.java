
/**
 * 
 */
package lense.compiler.ast;


import lense.compiler.ast.LenseAstNode;
import lense.compiler.ast.TypedNode;
import compiler.typesystem.TypeDefinition;


/**
 * 
 */
public class ExpressionNode extends LenseAstNode implements TypedNode {

	private TypeDefinition type;

	public TypeDefinition getTypeDefinition() {
		return type;
	}

	public void setTypeDefinition(TypeDefinition type) {
		this.type = type;
	}
}
