package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.java.PlatformSpecific;


public final class Natural64 implements Natural{
	
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
		return new BigNatural(asJavaBigInteger());
	}

	@Override
	public BigInteger asJavaBigInteger() {
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



	public int compareTo(Natural other ){
		if (other instanceof Natural64){
			return Long.compareUnsigned(this.value, ((Natural64)other).value );
		} else {
			return asJavaBigInteger().compareTo(other.asJavaBigInteger());
		}
	}

	public final HashValue hashValue(){
		return new HashValue (Long.hashCode(this.value));
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

	public boolean equalsTo(Any other) {
		return this.compareWith(other).nativeValue() == 0;
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
		return new BigInt(asJavaBigInteger());
	}

	@Override
	public int toPrimitiveInt() {
		if (Long.compareUnsigned(this.value, java.lang.Integer.MAX_VALUE) <= 0){
			return (int)this.value;
		}
		throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("To big for a primitive int"));
	}

	@Override
	public int modulus(int n) {
		return (int)(this.value % n);
	}

	@Override
	public boolean isInInt32Range() {
		return this.value <= (long)java.lang.Integer.MAX_VALUE;
	}


	public Natural remainder(Natural other) {
		if (other instanceof Natural64){
			return new Natural64(this.value % ((Natural64)other).value);
		} else {
			return new BigNatural(asJavaBigInteger().remainder(other.asJavaBigInteger()));
		}
	}

	@Override
	public Natural wholeDivide(Natural other) {
		if (other instanceof Natural64){
			return new Natural64(this.value / ((Natural64)other).value);
		} else {
			return new BigNatural(asJavaBigInteger().divide(other.asJavaBigInteger()));
		}
	}

	@Override
	public boolean isPositive() {
		return this.value != 0;
	}

	public Natural wrapMinus(Natural other) {
		if (other.compareTo(this) >= 0) {
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
		} else if (other instanceof Integer && ((Integer) other).isPositive() && other.compareTo(MAX) < 0){
			return this.plus(new Natural64(other.toString()));
		} else {
			return this.promoteNext().plus(other);
		}
	}





}

