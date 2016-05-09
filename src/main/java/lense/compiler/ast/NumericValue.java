/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.FixedTypeVariable;


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
		this.setTypeVariable(new FixedTypeVariable(type));
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
