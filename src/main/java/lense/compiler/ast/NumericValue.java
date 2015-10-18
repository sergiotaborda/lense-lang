/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.LiteralExpressionNode;
import compiler.typesystem.TypeDefinition;


/**
 * 
 */
public class NumericValue extends LiteralExpressionNode {

	private Number number;

	public NumericValue (){};
	
	/**
	 * @param n
	 */
	public void setValue(Number n, TypeDefinition type) {
		this.number = n;
		this.setTypeDefinition(type);
	}
	
	public String toString(){
		return number.toString();
	}

	/**
	 * @return
	 */
	public Number getValue() {
		return number;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLiteralValue() {
		return number.toString();
	}

}
