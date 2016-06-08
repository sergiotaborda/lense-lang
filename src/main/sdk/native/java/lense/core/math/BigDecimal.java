package lense.core.math;

import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;

public class BigDecimal extends Decimal {

	@Constructor
	public static BigDecimal constructor (){
		return new BigDecimal();
	}
	
	private java.math.BigDecimal value = java.math.BigDecimal.ZERO;
	
	private BigDecimal(){
		
	}
	
	@Override @Native
	protected java.math.BigDecimal getNativeBig() {
		return value;
	}
	
	public Int32 compareTo(Real other){
		return super.compareTo(other);
	}

}
