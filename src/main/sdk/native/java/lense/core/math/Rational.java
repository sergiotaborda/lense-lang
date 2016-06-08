package lense.core.math;

import java.math.BigDecimal;

import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;

public class Rational extends Real{

	
	@Constructor
	public static Rational constructor(Integer n , Integer d){
		return new Rational(n,d);
	}
	
	public Integer numerator;
	public Integer denominator;
	
	public Rational(Integer n, Integer d) {
		// TODO reduction
		numerator = n;
		denominator = d;
	}

	
	@Override @Native
	protected BigDecimal getNativeBig() {
		return new BigDecimal(numerator.getNativeBig()).divide(new BigDecimal(denominator.getNativeBig()));
	}
	
	public Int32 compareTo(Real other){
		return super.compareTo(other);
	}
	
}
