package lense.core.math;

import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.java.Signature;


@Signature(":lense.core.math.Number:lense.core.math.Comparable<lense.core.math.Real>&lense.core.math.SignedNumber")
@PlatformSpecific
public interface Real extends Number, Comparable , SignedNumber  {


//	@Override @MethodSignature (returnSignature = "lense.core.math.Comparison", paramsSignature="lense.core.math.Real", overloaded = true , declaringType="lense.core.math.Comparable")
//	public Comparison compareWith(Any other) {
//		if ( other instanceof Real) {
//			return this.compareWith((Real)other);
//		}
//		throw new ClassCastException();
//	}
//	
//	@PlatformSpecific
//	public Comparison compareWith(Real other){
//		final Integer difference = this.minus(other).sign();
//		if (difference.isZero()){
//			return Equal.constructor();
//		} else if (difference.isNegative()){
//			return Smaller.constructor();
//		} else {
//			return Greater.constructor();
//		}
//	}
//
//	protected abstract BigDecimal promoteToBigDecimal();

	public Real abs();

	public Real plus (Real other);
	public Real minus (Real other);
	public Real multiply(Real other);

	public Real divide(Real other);
	public Real raiseTo(Real other);

	public default Complex plus (Imaginary other){
		return Complex.constructor(this, other.real());	
	}

	public default Complex minus (Imaginary other){
		return Complex.constructor(this, other.real().symmetric());
	}

	public default Imaginary multiply(Imaginary other){
		return Imaginary.valueOf(this.multiply(other.real()).symmetric());
	}

	public default Imaginary divide(Imaginary other){
		return Imaginary.valueOf(this.divide(other.real()));
	}


	public Real symmetric();

	public boolean isZero();
	public boolean isOne();
	public boolean isNaN();
	public boolean isNegativeInfinity();
	public boolean isPositiveInfinity();
	public boolean isInfinity();
	
	public abstract Integer sign();

	public abstract Integer floor();
	
	public abstract Integer ceil();
	
	public abstract boolean isWhole();

	@Override
	public default boolean isNegative() {
		return this.sign().isNegative();
	}

	@Override
	public default boolean isPositive() {
		return this.sign().isPositive();
	}

	public Decimal asDecimal();
}
