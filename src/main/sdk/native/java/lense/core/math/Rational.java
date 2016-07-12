package lense.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;

public class Rational extends Real{

	
	@Constructor
	public static Rational constructor(Integer n , Integer d){
		return new Rational(n,d);
	}
	
	public BigInteger numerator;
	public BigInteger denominator;
	
	private Rational(Integer n, Integer d) {
		this(n.getNativeBig(),  d.getNativeBig());
	}

	Rational(BigInteger n, BigInteger d) {
		numerator = n;
		denominator = d;
	}
	
	@Override @Native
	protected BigDecimal getNativeBig() {
		return new BigDecimal(numerator).divide(new BigDecimal(denominator));
	}
	
	public Int32 compareTo(Real other){
		return super.compareTo(other);
	}
	
	
	@Override
	public Boolean equalsTo(Any other) {
		return other instanceof Rational ?  equalsTo((Rational)other) : Boolean.FALSE;
	}
	
	public Boolean equalsTo(Rational other) {
		return  Boolean.valueOfNative(this.numerator.compareTo(other.numerator) == 0 && this.denominator.compareTo(other.denominator) == 0);
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(hashCode());
	}
	
	@Override
	public final int hashCode() {
		return numerator.hashCode() ^ denominator.hashCode();
	}
	
	public String toString(){
		return this.numerator.toString() + "/" + this.denominator.toString();
	}
}
