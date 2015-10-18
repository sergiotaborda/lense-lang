/**
 * 
 */
package lense.compiler.crosscompile.java.ast;

import compiler.typesystem.TypeDefinition;



/**
 * 
 */
public class NumericValue extends LiteralExpressionNode {

	private Number number;
	
	public NumericValue(){}
	
	public NumericValue (Number n){
		this.number = n;
	}

	/**
	 * @param n
	 */
	public void setValue(Number n, TypeDefinition type) {
		this.number = n;
		this.type = type;
	}
	
	public String toString(){
		return number.toString();
	}

}
