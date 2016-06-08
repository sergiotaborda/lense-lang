package lense.core.math;

import java.math.BigDecimal;

import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;

public abstract class Real extends Number{

	@Constructor
	public static Real constructor(){
		return Rational.constructor(Int32.valueOfNative(0), Int32.valueOfNative(1));
	}
	
	public Int32 compareTo(Real other){
		Real r = (Real)other;
		return Int32.valueOfNative(this.getNativeBig().compareTo(r.getNativeBig()));
	}

	@Native
	protected abstract BigDecimal getNativeBig();
}
