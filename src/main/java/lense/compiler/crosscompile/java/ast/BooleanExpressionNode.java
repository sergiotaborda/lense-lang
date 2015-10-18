/**
 * 
 */
package lense.compiler.crosscompile.java.ast;

import lense.compiler.crosscompile.java.JavaType;

import compiler.typesystem.TypeDefinition;


/**
 * 
 */
public class BooleanExpressionNode extends ExpressionNode {

	public TypeDefinition getTypeDefinition() {
		return JavaType.Boolean;
	}

}
