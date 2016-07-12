package lense.core.math;

import java.math.BigDecimal;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;

public class Decimal64 extends Decimal{

	@Constructor
	public static Decimal64 constructor (){
		return new Decimal64();
	}
	
	private double value = 0f;
	
	private Decimal64(){
		
	}
	
	@Override @Native
	protected BigDecimal getNativeBig() {
		return new BigDecimal(value);
	}
	
	public Int32 compareTo(Real other){
		return super.compareTo(other);
	}
	
	
	@Override
	public Boolean equalsTo(Any other) {
		return Boolean.valueOfNative(other instanceof Decimal64 && Double.compare(((Decimal64)other).value ,this.value) == 0);
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(hashCode());
	}
	
	@Override
	public final int hashCode() {
		return Double.hashCode(value);
	}
}
