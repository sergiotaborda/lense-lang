package lense.core.math;

import java.math.BigDecimal;

import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;

public class Decimal32 extends Decimal{

	@Constructor
	public static Decimal32 constructor (){
		return new Decimal32();
	}
	
	private float value = 0f;
	
	private Decimal32(){
		
	}

	@Override @Native
	protected BigDecimal getNativeBig() {
		return new BigDecimal(value);
	}
	
	public Int32 compareTo(Real other){
		return super.compareTo(other);
	}
}
