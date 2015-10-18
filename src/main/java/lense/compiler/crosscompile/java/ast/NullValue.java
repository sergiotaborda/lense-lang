/**
 * 
 */
package lense.compiler.crosscompile.java.ast;

import lense.compiler.crosscompile.java.JavaType;
import compiler.typesystem.TypeDefinition;



/**
 * 
 */
public class NullValue extends LiteralExpressionNode {

	public TypeDefinition getTypeDefinition() {
		return JavaType.NullType;
	}
}
