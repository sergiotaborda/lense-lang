package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.NonNull;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.java.ValueClass;

@ValueClass
public final class BigInt implements Integer {

    private BigInteger value;

    @Constructor(paramsSignature = "")
    public static BigInt constructor(){
    	return new BigInt(BigInteger.ZERO);
    }
    
	BigInt(BigInteger n) {
		this.value = n;
	}

	@Override
	public Integer plus(Integer other) {
		return new BigInt(this.value.add(other.asJavaBigInteger()));
	}
	
	@Override
	public Integer minus(Integer other) {
		return new BigInt(this.value.subtract(other.asJavaBigInteger()));
	}
			
	@Override
	@PlatformSpecific
	public BigInteger asJavaBigInteger() {
		return value;
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
	public Integer multiply(Integer other) {
		return new BigInt(this.value.multiply(other.asJavaBigInteger()));
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
        } else if (other.compareTo(Int32.TWO) == 0){
            return  this.multiply(this);
        } else if (other.compareTo(Int32.THREE) == 0){
            return  this.multiply(this).multiply(this);
        } else if (other.isInInt32Range()){
            return new BigInt(this.value.pow(other.toPrimitiveInt()));
        } else {
            // TODO resolve calculation. possible too big
            throw new UnsupportedOperationException("raiseTo a number greater than Int32.MAX is not support yet");
        }
        
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

	@PlatformSpecific
    @Override
    public int toPrimitiveInt() {
        return value.intValue();
    }

	
	@Override
	public Integer wholeDivide(Integer other) {
		return new BigInt(this.asJavaBigInteger().divide(other.asJavaBigInteger()));
	}



	public String toString() {
		return String.valueOf(this.value);
	}


	public boolean equals(Object other) {
		return other instanceof Any && equalsTo((Any)other);
	}
	
	public boolean equalsTo(Any other) {
		return this.compareWith(other).nativeValue() == 0;
	}

	
    @Override
	public Comparison compareWith(Any other) {
    	if (other instanceof Integer) {
			return Comparison.valueOfNative(this.value.compareTo(((Integer) other).asJavaBigInteger()));
		} else  if (other instanceof Number && other instanceof Comparable) {
			 if (this.toString().equals(other.toString())){
				 return Comparison.valueOfNative(0);
			 }
			 return BigDecimal.valueOfNative(this.toString()).compareWith(other);
		} 
		throw new ClassCastException("Cannot compare to " + other.getClass().getName());
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
	


 
}
