package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
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

	
	@Override
	public Boolean equalsTo(Any other) {
		return Boolean.valueOfNative(other instanceof Int64 && ((BigDecimal)other).value.compareTo(this.value) == 0);
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(this.value.hashCode());
	}
	@Override
	public final int hashCode() {
		return value.hashCode();
	}
}
