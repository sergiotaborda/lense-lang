package lense.core.math;

public class Rational__Peer {

	
	public static Rational fraction(Whole numerator, Whole denominator) {
		return BigRational.constructor(numerator.asInteger(), denominator.asInteger());
	}

	public static Rational  one() {
		return BigRational.one();
	}

	public static Rational zero() {
		return BigRational.zero();
	}
	

	public static Rational valueOf(Whole other) {
		return BigRational.valueOf(other);
	}

	public static Rational valueOf(Integer other) {
		return BigRational.valueOf(other);
	}
}
