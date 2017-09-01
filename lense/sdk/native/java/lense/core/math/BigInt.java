package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.NonNull;

public class BigInt extends Integer {

    private BigInteger value;

    @Constructor
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
	protected BigInteger asJavaBigInteger() {
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
                return Integer.ONE;
            }
            return this;
        } else if (this.isOne()){
            return Integer.ONE;
        } else if (other.isZero()){
            return Integer.ONE;
        } else if (other.isOne()){
            return this;
        } else if (other.compareTo(Integer.valueOfNative(2)) == 0){
            return  this.multiply(this);
        } else if (other.compareTo(Integer.valueOfNative(3)) == 0){
            return  this.multiply(this).multiply(this);
        } else if (other.isInInt32Range()){
            return new BigInt(this.value.pow(other.toPrimitiveInt()));
        } else {
            // TODO resolve calculation. possible too big
            throw new UnsupportedOperationException("raiseTo a number biger than Int32.MAX is not support yet");
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

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public Integer signum() {
		return new Int32(value.signum());
	}

    @Override
    public Int32 toInt32() {
        return Int32.valueOfNative(this.value.intValue());
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

    @Override
    public int toPrimitiveInt() {
        return value.intValue();
    }
 
}
