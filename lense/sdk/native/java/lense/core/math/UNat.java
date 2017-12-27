package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.HashValue;


public final class UNat extends ScalableNatural{

	long value; // unsigned
	
	@Override
	public Natural plus(Natural other) {
		try {
			long o = ((UNat)other).value;
			long r =  value + o ;
			if (value < 0 || (Long.compareUnsigned(r, value) < 0 || Long.compareUnsigned(r, o) < 0) ) {
				return this.promoteNext().plus(other);
			}
			return new UNat(r);
		} catch (ClassCastException e){
			return this.promoteNext().plus(other);
		}		
	}
	
	@Override
	public Natural multiply(Natural other) {
		try {
			long o = ((UNat)other).value;
			
			int a_bits=highestOneBitPosition(this.value), b_bits=highestOneBitPosition(o);
		    if (a_bits+b_bits<=32){
				return new UNat( value * o );
		    
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
	
	UNat(long val){
		this.value = val;
	}

	public UNat(String value) {
		this.value = Long.parseUnsignedLong(value);
	}

	@Override
	protected BigInteger asJavaBigInteger() {
		return new BigInteger(this.toString());
	}

	@Override
	protected int maxByteCount() {
		return 64;
	}

	@Override
	protected Natural promoteNext() {
		return new BigNatural(asJavaBigInteger());
	}

	@Override
	public Natural successor() {
		long r =  value + 1 ;
		if (Long.compareUnsigned(r, value) < 0  ) {
			return this.promoteNext().successor();
		}
		return new UNat(r);
	}

	
	
	public int compareTo(Natural other ){
		if (other instanceof UNat){
			return Long.compareUnsigned(this.value, ((UNat)other).value );
		} else {
			return asJavaBigInteger().compareTo(other.asJavaBigInteger());
		}
	}

	public final HashValue hashValue(){
		return new HashValue (Long.hashCode(this.value));
	}
	
	public final lense.core.lang.String asString(){
		return lense.core.lang.String.valueOfNative(java.lang.Long.toUnsignedString(value)); 
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
		return new UNat(value - 1);
	}

	@Override
	protected Integer asInteger() {
	    if (value < (long)java.lang.Integer.MAX_VALUE){
            return ScalableInt32.valueOf((int)value);
        } else if (value < Long.MAX_VALUE){
			return ScalableInt64.valueOf(value);
		} 
		return new BigInt(new BigInteger(this.toString()));
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
         if (other instanceof UNat){
             return new UNat(this.value % ((UNat)other).value);
         } else {
             return new BigNatural(asJavaBigInteger().remainder(other.asJavaBigInteger()));
         }
    }

    @Override
    public Natural wholeDivide(Natural other) {
        if (other instanceof UNat){
            return new UNat(this.value / ((UNat)other).value);
        } else {
            return new BigNatural(asJavaBigInteger().divide(other.asJavaBigInteger()));
        }
    }

	@Override
	public boolean isPositive() {
		return this.value != 0;
	}



  


}

