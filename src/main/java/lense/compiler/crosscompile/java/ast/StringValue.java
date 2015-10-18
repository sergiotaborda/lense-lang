/**
 * 
 */
package lense.compiler.crosscompile.java.ast;

import lense.compiler.crosscompile.java.JavaType;

import compiler.typesystem.TypeDefinition;


/**
 * 
 */
public class StringValue extends LiteralExpressionNode {

	private String value;
	
	public TypeDefinition getTypeDefinition() {
		return JavaType.String;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}


}
