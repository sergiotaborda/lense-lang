package lense.core.math;

import java.math.BigInteger;

import lense.core.collections.NativeOrdinalProgression;
import lense.core.collections.Progression;
import lense.core.lang.Any;
import lense.core.lang.AnyValue;
import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.NonNull;
import lense.core.lang.java.Primitives;
import lense.core.lang.java.ValueClass;
import lense.core.lang.reflection.Type;

@ValueClass
public final class BigNatural implements Natural , BigDecimalConvertable , BigIntegerConvertable , AnyValue{

	@Constructor(isImplicit = false, paramsSignature = "lense.core.lang.String")
    public static BigNatural parse(lense.core.lang.String other) {
	    return new BigNatural(new BigInteger(other.toString()));
	}
	
	
	private BigInteger value;

	public BigNatural(long value) {
		this.value = BigInteger.valueOf(value);
	}

	BigNatural(BigInteger value) {
		this.value = value;
	}

	@Override
	public Natural plus(Natural other) {

		if (other instanceof BigIntegerConvertable) {
			return new BigNatural(this.value.add(((BigIntegerConvertable) other).toJavaBigInteger()));
		}
		return new BigNatural(this.value.add(new BigInteger(other.toString())));
	}

	@Override
	public Natural multiply(Natural other) {
		if (other instanceof BigIntegerConvertable) {
			return new BigNatural(this.value.multiply(((BigIntegerConvertable) other).toJavaBigInteger()));
		}
		return new BigNatural(this.value.multiply(new BigInteger(other.toString())));
	}


	public BigInteger toJavaBigInteger(){
		return value;
	}

	public lense.core.lang.String asString(){
		return lense.core.lang.String.valueOfNative(value.toString()); 
	}

	@Override
	public Natural successor() {
		return new BigNatural(this.value.add(BigInteger.ONE));
	}

	@Override
	public boolean isZero() {
		return value.signum() == 0;
	}

	@Override
	public boolean isOne() {
		return value.compareTo(BigInteger.ONE) == 0;
	}

	@Override
	public Natural predecessor() {
		if (value.signum() == 0){
			throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("min predecessor reached"));
		}
		return new BigNatural(value.subtract(BigInteger.ONE));
	}

	@Override
	public Integer asInteger() {
		return new BigInt(value);
	}

	public int toPrimitiveInt() {
		return this.value.intValue();
	}

	@Override
	public int modulus(int n) {
		return this.value.remainder(BigInteger.valueOf(n)).intValueExact();
	}

	public Natural raiseTo( Natural other){
		if (this.isZero()){
			if (other.isZero()){
				return Natural64.ONE;
			}
			return this;
		} else if (this.isOne()){
			return this;
		} else if (other.isZero()){
			return Natural64.ONE;
		} else if (other.isOne()){
			return this;
		} else if (isInInt32Range(other)){
			return new BigNatural(this.value.pow(NativeNumberFactory.naturalToPrimitiveInt(other))).reduce();
		} else {
			// TODO resolve calculation. possible too big
			throw new UnsupportedOperationException("raiseTo a number biger than Int32.MAX is not support yet");
		}
	}

	
    public Natural reduce() {
		
		if (this.compareWith(Natural64.MAX).isSmaller()) {
			return Natural64.parseNative(this.value.toString());
		}
		
		return this;
		
	}

	private boolean isInInt32Range(Natural other) {
		return other.compareWith(Natural64.INT32_MAX).isSmaller();
	}
	
	public Real raiseTo( Real other){
		return this.asReal().raiseTo(other);
	}

	public Natural remainder(Natural other) {
		if (other instanceof BigIntegerConvertable) {
			return new BigNatural(this.value.remainder(((BigIntegerConvertable) other).toJavaBigInteger())).reduce();
		}
		return new BigNatural(this.value.remainder(new BigInteger(other.toString()))).reduce();
	}

	@Override
	public Natural wholeDivide(Natural other) {
		return divide(other).floor().abs();
	}

	@Override
	public boolean isPositive() {
		return this.value.signum() > 0;
	}


	@Override
	public Natural wrapMinus(Natural other) {

		BigInteger rest;
		if (other instanceof BigIntegerConvertable) {
			rest = this.value.subtract(((BigIntegerConvertable) other).toJavaBigInteger());
		} else {
			rest = this.value.subtract(new BigInteger(other.toString()));
		}

		if (rest.signum() <=0) {
			return Natural64.ZERO;
		}
		return new BigNatural(rest).reduce();
	}


	public final HashValue hashValue(){
		return new HashValue(value.hashCode());
	}

	public String toString() {
		return value.toString();
	}

	public int hashCode() {
		return value.hashCode();
	}

	public boolean equals(Object other) {
		return other instanceof Any && equalsTo((Any)other);
	}

	@Override
	public boolean equalsTo(Any other) {
		if (!(other instanceof Number) && !(other instanceof Comparable)) {
			return false;
		}
		return NativeNumberFactory.compareNumbers(this, (Number)other) == 0;
	}

	@Override
	public Whole plus(Whole other) {
		return new BigInt(this.value.add(new BigInteger(other.toString())));
	}

	@Override
	public Rational divide(Whole other) {
		return Rational.fraction(this.asInteger(), other.asInteger());
	}

	@Override
	public Real asReal() {
		return Rational.valueOf(this.asInteger());
	}

	@Override
	public Natural gcd(Whole other) {
		BigInteger gcd =  this.toJavaBigInteger().gcd(new BigInteger(other.toString()));

		if (gcd.bitLength() < 63){
			return new Natural64(gcd.longValue());
		} else {
			return new BigNatural(gcd);
		}
	}

	@Override
	public Type type() {
		return Type.fromName(this.getClass().getName());
	}

	@Override
	public Comparison compareWith(Any other) {
		if (other instanceof BigIntegerConvertable) {
			return Primitives.comparisonFromNative(this.toJavaBigInteger().compareTo(((BigIntegerConvertable) other).toJavaBigInteger()));
		} else  if (other instanceof Number && other instanceof Comparable) {
			if (this.toString().equals(other.toString())){
				return Primitives.comparisonFromNative(0);
			}
			return BigDecimal.valueOfNative(this.toString()).compareWith(other);
		} 
		throw new ClassCastException("Cannot compare to " + other.getClass().getName());
	}

	@Override
	public java.math.BigDecimal toBigDecimal() {
		return new java.math.BigDecimal(value);
	}

	@Override
	public @NonNull Progression upTo( Natural other) {
		if (other instanceof Natural) {
			return new NativeOrdinalProgression(this, other, true);
		}
		throw new ClassCastException("other is not a Natural");
	}

	@Override
	public Progression upToExclusive(Natural other) {
		if (other instanceof Natural) {
			return new NativeOrdinalProgression(this, other, false);
		}
		throw new ClassCastException("other is not a Natural");
	}

	@Override
	public Integer wholeDivide(Integer other) {
		return divide(other).floor();
	}

	@Override
	public Whole minus(Whole other) {
		if (other instanceof Natural) {
			return this.minus((Natural)other);
		} else {
			return this.asInteger().minus(other);
		}
	}

	@Override
	public Natural wrapPlus(Natural other) {
		return plus(other);
	}

	@Override
	public Natural wrapMultiply(Natural other) {
		return multiply(other);
	}

	@Override
	public Integer minus(Natural other) {
		return this.asInteger().minus(other);
	}

	@Override
	public Integer symmetric() {
		return this.asInteger().symmetric();
	}

	@Override
	public boolean isNegative() {
		return false;
	}

	@Override
	public Rational raiseTo(Integer other) {
		if (this.isZero()){
			if (other.isZero()){
				return Rational.one();
			}
			return Rational.zero();
		} else if (this.isOne()){
			return Rational.one();
		} else if (other.isZero()){
			return Rational.one();
		} else if (other.isOne()){
			return Rational.valueOf(this.asInteger());
		}  else if (other.isNegative()){
			return Rational.fraction(Int32.ONE, this.raiseTo(other.abs()).asInteger());
		} else {
			return Rational.valueOf( this.raiseTo(other.abs()).asInteger());
		}
	}

	@Override
	public Integer multiply(Integer other) {
		return this.asInteger().multiply(other);
	}

	@Override
	public Natural abs() {
		return this;
	}
	
	@Override
	public Complex plus(Imaginary n) {
		return Complex.rectangular(this.asReal(), n.real());
	}

	@Override
	public Complex minus(Imaginary n) {
		return Complex.rectangular(this.asReal(), n.real().symmetric());
	}

	@Override
	public Imaginary multiply(Imaginary n) {
		return ImaginaryOverReal.valueOf(this.asReal().multiply(n.real()));
	}

	@Override
	public Imaginary divide(Imaginary n) {
		return ImaginaryOverReal.valueOf(this.asReal().divide(n.real()));
	}

	@Override
	public Whole wholeDivide(Whole other) {
		if (other instanceof Natural) {
			return wholeDivide((Natural)other);
		}
		return this.asInteger().wholeDivide(other);
	}

	@Override
	public Whole remainder(Whole other) {
		if (other instanceof Natural) {
			return remainder((Natural)other);
		}
		return this.asInteger().remainder(other);
	}
}
