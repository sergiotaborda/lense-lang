package lense.core.math;

import java.math.BigInteger;

import lense.core.collections.NativeOrdinalProgression;
import lense.core.collections.Progression;
import lense.core.lang.Any;
import lense.core.lang.AnyValue;
import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.NonNull;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.java.Primitives;
import lense.core.lang.java.ValueClass;
import lense.core.lang.reflection.Type;

@ValueClass
public final class BigInt implements Integer , BigIntegerConvertable , AnyValue {

    private static BigInt ZERO = new BigInt(BigInteger.ZERO);

	private BigInteger value;

	@Constructor(paramsSignature = "")
	public static BigInt constructor(){
		return ZERO;
	}

	@Constructor(isImplicit = false, paramsSignature = "lense.core.lang.String")
    public static BigInt parse(lense.core.lang.String other) {
	    return new BigInt(new BigInteger(other.toString()));
	}
	
	@Constructor(isImplicit = true, paramsSignature = "lense.core.math.Natural")
	public static BigInt valueOf(Natural other) {

		if (other instanceof BigIntegerConvertable) {
			return new BigInt( ((BigIntegerConvertable) other).toJavaBigInteger());
		}

		return new BigInt(new BigInteger(other.toString()));
	}

	public static BigInt valueOfNative(long other) {
		return new BigInt( BigInteger.valueOf(other));
	}
	
	BigInt(BigInteger n) {
		this.value = n;
	}

	@Override
	public Integer plus(Integer other) {

		if (other instanceof BigIntegerConvertable) {
			return new BigInt(this.value.add(((BigIntegerConvertable) other).toJavaBigInteger()));
		}

		return new BigInt(this.value.add(new BigInteger(other.toString())));
	}

	@Override
	public Integer minus(Integer other) {
		if (other instanceof BigIntegerConvertable) {
			return new BigInt(this.value.subtract(((BigIntegerConvertable) other).toJavaBigInteger()));
		}

		return new BigInt(this.value.subtract(new BigInteger(other.toString())));
	}

	@Override
	public Integer successor() {
		return new BigInt(this.value.add(BigInteger.ONE));
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
	public Integer predecessor() {
		return new BigInt(this.value.subtract(BigInteger.ONE));
	}

	@Override
	public BigInt multiply(Integer other) {
		if (other instanceof BigIntegerConvertable) {
			return new BigInt(this.value.multiply(((BigIntegerConvertable) other).toJavaBigInteger()));
		}

		return new BigInt(this.value.multiply(new BigInteger(other.toString())));

	}


	@Override
	public Integer wholeDivide(Integer other) {
		if (other instanceof BigIntegerConvertable) {
			return new BigInt(this.value.divide(((BigIntegerConvertable) other).toJavaBigInteger()));
		}

		return new BigInt(this.value.divide(new BigInteger(other.toString())));

	}

	@Override
	public Integer remainder(Integer other) {
		if (other instanceof BigIntegerConvertable) {
			return new BigInt(this.value.remainder(((BigIntegerConvertable) other).toJavaBigInteger()));
		}

		return new BigInt(this.value.remainder(new BigInteger(other.toString())));

	}

	@Override
	public @NonNull Integer raiseTo(Natural other) {
		if (this.isZero()){
			if (other.isZero()){
				return Int32.ONE;
			}
			return this;
		} else if (this.isOne()){
			return Int32.ONE;
		} else if (other.isZero()){
			return Int32.ONE;
		} else if (other.isOne()){
			return this;
		} else if (other.compareWith(Int32.TWO).isEqual()){
			return  this.multiply(this).reduce();
		} else if (other.compareWith(Int32.THREE).isEqual()){
			return  this.multiply(this).multiply(this).reduce();
		} else if (isInInt32Range(other)){
			return new BigInt(this.value.pow(NativeNumberFactory.naturalToPrimitiveInt(other))).reduce();
		} else {
			// TODO resolve calculation. possible too big
			throw new UnsupportedOperationException("raiseTo a value greater than Int32.MAX is not support yet");
		}

	}

	private boolean isInInt32Range(Natural other) {

		return other.compareWith(Natural64.INT32_MAX).isSmaller();
	}
	
	Integer reduce() {
		
		if (this.value.abs().bitLength() < 31) {
			return Int32.valueOfNative(this.value.intValueExact());
		} else if (this.value.abs().bitLength() < 63) {
			return Int64.valueOfNative(this.value.longValueExact());
		} 
		
		return this;
	}
	

	@Override
	public Natural abs() {
		return new BigNatural(this.value.abs());
	}

	public lense.core.lang.String asString(){
		return lense.core.lang.String.valueOfNative(value.toString());
	}

	@Override
	public Integer symmetric() {
		return new BigInt(this.value.negate());
	}

	@PlatformSpecific
	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public Integer sign() {
		return new Int32(value.signum());
	}

	@Override
	public boolean isNegative() {
		return this.value.compareTo(BigInteger.ZERO) < 0;
	}

	@Override
	public boolean isPositive() {
		return this.value.compareTo(BigInteger.ZERO) > 0;
	}


	@Override
	public HashValue hashValue() {
		return new HashValue(this.value.hashCode());
	}



	public String toString() {
		return String.valueOf(this.value);
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



	public @NonNull Integer raiseTo(int exponent) {
		if (this.isZero()){
			if (exponent == 0){
				return Int32.ONE;
			}
			return this;
		} else if (this.isOne()){
			return Int32.ONE;
		} else if (exponent == 0){
			return Int32.ONE;
		} else if (exponent == 1){
			return this;
		} else if (exponent == 2){
			return  this.multiply(this);
		} else if (exponent == 3){
			return  this.multiply(this).multiply(this);
		}

		return new BigInt(this.value.pow(exponent));
	}


	@Override
	public Progression upTo(Any end) {
		return new NativeOrdinalProgression(this, (BigInt)end, true);
	}

	@Override
	public Progression upToExclusive(Any end) {
		return new NativeOrdinalProgression(this, (BigInt)end, false);
	}


	@Override
	public BigInteger toJavaBigInteger() {
		return this.value;
	}

	@Override
	public Rational divide(Whole other) {
		return Rational.constructor(this, other.asInteger());
	}

	@Override
	public Real asReal() {
		return Rational.constructor(this, Int32.ONE);
	}

	@Override
	public Integer asInteger() {
		return this;
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
	public Whole minus(Whole other) {
		return minus(other.asInteger());
	}

	@Override
	public Integer wholeDivide(Natural other) {
		return wholeDivide(other.asInteger());
	}

	@Override
	public Whole plus(Whole other) {
		return plus(other.asInteger());
	}

	@Override
	public @NonNull Real raiseTo(Real other) {
		if ( other.isZero()) {
			return Rational.ONE;
		} else if (other.isOne()) {
			return this.asReal();
		} else if (other.isWhole()) {
			Integer whole = other.floor();
	
			Rational power = Rational.constructor(raiseTo(whole.abs()), Int32.ONE);
			
			if (whole.sign().isNegative()) {
				power = power.invert();
			} 
			
			return power;
		}
		return BigDecimal.constructor(Rational.constructor(this, Int32.ONE)).raiseTo(other);
	}
	
	@Override
	public Complex plus(Imaginary n) {
		return Complex.retangular(this.asReal(), n.real());
	}

	@Override
	public Complex minus(Imaginary n) {
		return Complex.retangular(this.asReal(), n.real().symmetric());
	}

	@Override
	public Imaginary multiply(Imaginary n) {
		return Imaginary.valueOf(this.asReal().multiply(n.real()));
	}

	@Override
	public Imaginary divide(Imaginary n) {
		return Imaginary.valueOf(this.asReal().divide(n.real()));
	}

	@Override
	public java.math.BigDecimal toBigDecimal() {
		return new java.math.BigDecimal(value); 
	}
	
	@Override
	public Integer minus(Natural other) {
		return this.minus(other.asInteger());
	}

	@Override
	public Integer plus(Natural other) {
		return this.plus(other.asInteger());
	}

	@Override
	public Integer multiply(Natural other) {
		return this.multiply(other.asInteger());
	}

	@Override
	public Whole wholeDivide(Whole other) {
		return this.wholeDivide(other.asInteger());
	}

	@Override
	public Whole remainder(Whole other) {
		return this.remainder(other.asInteger());
	}

}
