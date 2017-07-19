/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;


/**
 * 
 */
public class NumericValue extends LiteralExpressionNode {

	private Number number;

	public NumericValue (){};
	
	public static  NumericValue zero (){
		NumericValue nv = new NumericValue();
		nv.setValue(0, LenseTypeSystem.Natural());
		return nv;
	};
	
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
