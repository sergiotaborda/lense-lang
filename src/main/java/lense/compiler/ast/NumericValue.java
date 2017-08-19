/**
 * 
 */
package lense.compiler.ast;

import java.math.BigDecimal;

import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;


/**
 * 
 */
public class NumericValue extends LiteralExpressionNode {

	private BigDecimal number;

	public NumericValue (){};
	
	public static  NumericValue zero (){
		NumericValue nv = new NumericValue();
		nv.setValue(BigDecimal.ZERO, LenseTypeSystem.Natural());
		return nv;
	};
	
	/**
	 * @param n
	 */
	public void setValue(BigDecimal n, TypeDefinition type) {
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

    public boolean isZero() {
        return number.signum() == 0;
    }


}
