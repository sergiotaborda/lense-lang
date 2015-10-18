/**
 * 
 */
package lense.compiler.crosscompile.java.ast;

import lense.compiler.crosscompile.java.JavaType;

import compiler.typesystem.TypeDefinition;



/**
 * 
 */
public class BooleanValue extends LiteralExpressionNode {

	
	private boolean value;
	public TypeDefinition getTypeDefinition() {
		return JavaType.Boolean;
	}
	public boolean isValue() {
		return value;
	}
	public void setValue(boolean value) {
		this.value = value;
	}
}
