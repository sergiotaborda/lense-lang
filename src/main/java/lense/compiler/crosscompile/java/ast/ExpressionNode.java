
/**
 * 
 */
package lense.compiler.crosscompile.java.ast;

import compiler.typesystem.TypeDefinition;


/**
 * 
 */
public class ExpressionNode extends JavaAstNode implements TypedNode {

	protected TypeDefinition type;

	public TypeDefinition getTypeDefinition() {
		return type;
	}


}
