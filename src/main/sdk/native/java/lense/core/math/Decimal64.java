package lense.core.math;

import java.math.BigDecimal;

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
}
