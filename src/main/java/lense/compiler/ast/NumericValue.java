/**
 * 
 */
package lense.compiler.ast;

import java.math.BigDecimal;

import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;


/**
 * 
 */
public class NumericValue extends LiteralExpressionNode {

	protected BigDecimal number;

	public NumericValue (){};
	
	public static  NumericValue zero (){
		NumericValue nv = new NumericValue();
		nv.setValue(BigDecimal.ZERO, LenseTypeSystem.Natural());
		return nv;
	};
	
	/**
	 * @param n
	 */
	public NumericValue setValue(BigDecimal n, TypeDefinition type) {
		this.number = n;
		this.setTypeVariable(type);
		return this;
	}
	
	public NumericValue setValue(BigDecimal n, TypeVariable type) {
		this.number = n;
		this.setTypeVariable(type);
		return this;
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
    

	public boolean isOne() {
		  return number.compareTo(BigDecimal.ONE) == 0;
	}

	@Override
	public void setTypeVariable(TypeVariable type) {
		TypeDefinition def = type.getTypeDefinition();
		if (this.getTypeVariable() == null || ( def != null && !def.getName().equals("lense.core.math.Number"))) {
			super.setTypeVariable(type);
		}
	} 
}
