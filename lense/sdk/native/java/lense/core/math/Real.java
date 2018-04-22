package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.MethodSignature;
import lense.core.lang.java.PlatformSpecific;

public abstract class Real extends Number implements Comparable , SignedNumber {

	public static final Rational ZERO = Rational.constructor(Int32.valueOfNative(0), Integer.ONE);
	public static final Rational ONE = Rational.constructor(Int32.valueOfNative(1), Integer.ONE);

	@Constructor(paramsSignature = "")
	public static Real constructor(){
		return Rational.constructor(Int32.valueOfNative(0), Integer.ONE);
	}

	@Constructor(isImplicit = true, paramsSignature="lense.core.math.Whole")
	public static Real valueOf(Whole other) {
		return Rational.constructor(other.asInteger(), Integer.ONE);
	}


	@Constructor(paramsSignature = "")
	public static Real zero(){
		return ZERO;
	}

	@Constructor(paramsSignature = "")
	public static Real one(){
		return ONE;
	}

	@Override @MethodSignature (returnSignature = "lense.core.math.Comparison", paramsSignature="lense.core.math.Real", overloaded = true , declaringType="lense.core.math.Comparable")
	public Comparison compareWith(Any other) {
		if ( other instanceof Real) {
			return this.compareWith((Real)other);
		}
		throw new ClassCastException();
	}
	
	@PlatformSpecific
	public Comparison compareWith(Real other){
		final Integer difference = this.minus(other).signum();
		if (difference.isZero()){
			return Equal.constructor();
		} else if (difference.isNegative()){
			return Smaller.constructor();
		} else {
			return Greater.constructor();
		}
	}

	protected abstract BigDecimal promoteToBigDecimal();

	public abstract Real abs();

	public abstract Real plus (Real other);
	public abstract Real minus (Real other);
	public abstract Real multiply(Real other);

	public Real wrapPlus(Real other) {
		return this.plus(other);
	}

	public Real wrapMinus(Real other) {
		return this.minus(other);
	}

	public Real wrapMultiply(Real other) {
		return this.multiply(other);
	}

	public abstract Real divide(Real other);
	public abstract Real raiseTo(Real other);

	public Complex plus (Imaginary other){
		return Complex.constructor(this, other.real());	
	}

	public Complex minus (Imaginary other){
		return Complex.constructor(this, other.real().symmetric());
	}

	public Imaginary multiply(Imaginary other){
		return Imaginary.valueOf(this.multiply(other.real()).symmetric());
	}

	public Imaginary divide(Imaginary other){
		return Imaginary.valueOf(this.divide(other.real()));
	}


	public abstract Real symmetric();

	public abstract boolean isZero();
	public abstract boolean isOne();

	public abstract Integer signum();

	public abstract Integer asInteger();

	public abstract boolean isWhole();

	@Override
	public boolean isNegative() {
		return this.signum().isNegative();
	}

	@Override
	public boolean isPositive() {
		return this.signum().isPositive();
	}


}
