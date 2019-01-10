package lense.core.math;

import lense.core.lang.java.Signature;


@Signature("::lense.core.math.Number&lense.core.math.Comparable<lense.core.math.Real>&lense.core.math.SignedNumber")
public interface Real extends Number, Comparable , SignedNumber {

	public Real abs();

	public Real plus (Real other);
	public Real minus (Real other);
	public Real multiply(Real other);

	public Real divide(Real other);
	public Real raiseTo(Real other);

	public default Complex plus (Imaginary other){
		return Complex.retangular(this, other.real());	
	}

	public default Complex minus (Imaginary other){
		return Complex.retangular(this, other.real().symmetric());
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
	public boolean isNegativeZero();
	
	public Integer sign();

	public Integer floor();
	
	public Integer ceil();
	
	public boolean isWhole();

	@Override
	public boolean isNegative();
	@Override
	public boolean isPositive();

}
