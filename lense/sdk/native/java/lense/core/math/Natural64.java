package lense.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import lense.core.collections.NativeOrdinalProgression;
import lense.core.collections.Progression;
import lense.core.lang.Any;
import lense.core.lang.AnyValue;
import lense.core.lang.HashValue;
import lense.core.lang.java.NonNull;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.java.Primitives;
import lense.core.lang.java.Signature;
import lense.core.lang.reflection.NativeType;
import lense.core.lang.reflection.Type;

@Signature("::lense.core.math.Natural")
public final class Natural64 implements Natural , BigDecimalConvertable , BigIntegerConvertable, AnyValue{
	
	public static final Number INT32_MAX = valueOfNative(java.lang.Integer.MAX_VALUE);
	public static Natural64 MAX = new Natural64("18446744073709551615");
	public static Natural64 ZERO = valueOfNative(0);
	public static Natural64 ONE = valueOfNative(1);
	
	@PlatformSpecific
	public static Natural64 valueOfNative(int value){
		if (value < 0){
			throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("A negative integer cannot be transformed to a Natural"));
		}
		return new Natural64(value);
	}

	@PlatformSpecific
	public static Natural64 valueOfNative(long value) {
		if (value < 0){
			throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("A negative integer cannot be transformed to a Natural"));
		}
		return new Natural64(value);
	}


	public static Natural64 parseNative(String value) {
		
		return new Natural64(Long.parseUnsignedLong(value));
	}

	long value; // unsigned

	@Override
	public Natural plus(Natural other) {
		try {
			long o = ((Natural64)other).value;
			long r =  value + o ;
			if (value < 0 || (Long.compareUnsigned(r, value) < 0 || Long.compareUnsigned(r, o) < 0) ) {
				return this.promoteNext().plus(other);
			}
			return new Natural64(r);
		} catch (ClassCastException e){
			return this.promoteNext().plus(other);
		}		
	}

	@Override
	public Natural multiply(Natural other) {
		try {
			long o = ((Natural64)other).value;

			int a_bits=highestOneBitPosition(this.value), b_bits=highestOneBitPosition(o);
			if (a_bits+b_bits<=32){
				return new Natural64( value * o );

			} else {
				return this.promoteNext().multiply(other);
			}



		} catch (ClassCastException e){
			return this.promoteNext().multiply(other);
		}		
	}

	private int highestOneBitPosition(long n){
		int bits=0;
		while ( n!=0) {
			++bits;
			n>>=1;
		};
		return bits;
	}

	Natural64(long val){
		this.value = val;
	}

	public Natural64(String value) {
		this.value = Long.parseUnsignedLong(value);
	}


	protected Natural promoteNext() {
		return new BigNatural(toJavaBigInteger());
	}

	@Override
	public BigInteger toJavaBigInteger() {
		return new BigInteger(this.toString());
	}




	@Override
	public Natural successor() {
		long r =  value + 1 ;
		if (Long.compareUnsigned(r, value) < 0  ) {
			return this.promoteNext().successor();
		}
		return new Natural64(r);
	}


	public String toString() {
		return java.lang.Long.toUnsignedString(value);
	}

	public int hashCode() {
		return Long.hashCode(this.value);
	}

	public boolean equals(Object other) {
		return other instanceof Any && equalsTo((Any)other);
	}

	
	@Override
	public boolean isZero() {
		return value == 0L;
	}

	@Override
	public boolean isOne() {
		return value == 1L;
	}

	@Override
	public Natural predecessor() {
		if (value == 0L){
			throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("min predecessor reached"));
		}
		return new Natural64(value - 1);
	}

	@Override
	public Integer asInteger() {
		if (Long.compareUnsigned(value, (long)java.lang.Integer.MAX_VALUE) < 0 ){
			return Int32.valueOfNative((int)value);
		} else if (Long.compareUnsigned(value, java.lang.Long.MAX_VALUE) < 0){
			return Int64.valueOfNative(value);
		} 
		return new BigInt(toJavaBigInteger());
	}
	
	public int toPrimitiveInt() {
		if (Long.compareUnsigned(this.value, java.lang.Integer.MAX_VALUE) <= 0){
			return (int)this.value;
		}
		throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("To big for a primitive int"));
	}

	@Override
	public boolean isPositive() {
		return this.value != 0;
	}

	public Natural wrapMinus(Natural other) {
		if (!other.compareWith(this).isSmaller()) {
			return ZERO;
		} else if (other instanceof Natural64) {
			return new Natural64(this.value - ((Natural64)other).value);
		} else {
			return this.promoteNext().wrapMinus(other);
		}
	}


	@Override
	public Whole plus(Whole other) {
		if (other instanceof Natural){
			return this.plus((Natural)other);
		} else if (other instanceof Integer && ((Integer) other).isPositive() && other.compareWith(MAX).isSmaller()){
			return this.plus(new Natural64(other.toString()));
		} else {
			return this.promoteNext().plus(other);
		}
	}

	
	@Override
	public Float asFloat() {
		return BigFloat.valueOf(this);
	}
	
    @Override
    public Comparison compareWith(Any other) {
    	
    	if (other instanceof Natural64) {
			return Primitives.comparisonFromNative(Long.compareUnsigned(this.value, ((Natural64) other).value));
		} else if (other instanceof RealLineElement){
        	return NativeNumberFactory.compareNumbers(this, (RealLineElement)other);
        }
        
    	throw new IllegalArgumentException("Cannot compare with " + other.toString());
    }
    
    @Override
    public boolean equalsTo(Any other) {
    	return (other instanceof RealLineElement) && compareWith(other).isEqual();
    }

	@Override
	public final HashValue hashValue(){
		return new HashValue(Long.hashCode(this.value));
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
	public lense.core.lang.String asString() {
		return lense.core.lang.String.valueOfNative(this.toString());
	}
	

	@Override
	public Type type() {
		return Type.forName(this.getClass().getName());
	}


	@Override
	public BigDecimal toBigDecimal() {
		return new BigDecimal(this.toJavaBigInteger());
	}
	
	@Override
	public Progression upTo(Any other) {
		if (other instanceof Natural) {
			return new NativeOrdinalProgression(this, (Natural)other, true);
		}
		throw new ClassCastException("other is not a Natural");
	}

	@Override
	public Progression upToExclusive(Any other) {
		if (other instanceof Natural) {
			return new NativeOrdinalProgression(this, (Natural)other, false);
		}
		throw new ClassCastException("other is not a Natural");
	}
	
	@Override
	public Integer wholeDivide(Integer other) {
		 return this.asInteger().wholeDivide(other);
	}
	

	@Override
	public Whole minus(Whole other) {
		return this.asInteger().minus(other);
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
	public Real raiseTo(Real other) {
		return this.asReal().raiseTo(other);
	}
	

	@Override
	public Natural raiseTo(Natural other) {
		return new BigNatural(this.toJavaBigInteger()).raiseTo(other);
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
	public @NonNull Natural abs() {
		return this;
	}

	@Override
	public Complex plus(Imaginary n) {
		return ComplexOverReal.rectangular(this.asReal(), n.real());
	}

	@Override
	public Complex minus(Imaginary n) {
		return ComplexOverReal.rectangular(this.asReal(), n.real().symmetric());
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
	public Natural wholeDivide(Natural other) {
		if (other instanceof Natural64){
			return new Natural64(this.value / ((Natural64)other).value);
		} else {
			return new BigNatural(toJavaBigInteger()).wholeDivide(other);
		}
	}
	
	public Natural remainder(Natural other) {
		if (other.isZero()){
			throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
		}  
		
		if (other instanceof Natural64 natural){
			return new Natural64(this.value % natural.value);
		} else {
			return new BigNatural(toJavaBigInteger()).remainder(other);
		}
	}

	@Override
	public Whole remainder(Whole other) {
		if (other.isZero()){
			throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
		}  
		
		if (other instanceof Natural) {
			return remainder((Natural)other);
		}
		return this.asInteger().remainder(other);
	}
	

	@Override
	public Whole modulo(Whole other) {
		// since this is always positive, remainder and module are the same
		return remainder(other);
	}
	
	@Override
	public Natural modulo(Natural other) {
		// since this is always positive, remainder and module are the same
		return remainder(other);
	}

	


}

