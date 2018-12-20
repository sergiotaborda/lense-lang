package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.Any;
import lense.core.lang.HashValue;

public final class BigNatural implements Natural{

	
	
    private BigInteger value;

    public BigNatural(long value) {
        this.value = BigInteger.valueOf(value);
    }

    BigNatural(BigInteger value) {
        this.value = value;
    }

    @Override
    public Natural plus(Natural other) {
        return new BigNatural(this.value.add(other.asJavaBigInteger()));
    }

    @Override
    public Natural multiply(Natural other) {
        return new BigNatural(this.value.multiply(other.asJavaBigInteger()));
    }

    public BigInteger asJavaBigInteger(){
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

    @Override
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
        } else if (other.isInInt32Range()){
            return new BigNatural(this.value.pow(other.toPrimitiveInt()));
        } else {
            // TODO resolve calculation. possible too big
            throw new UnsupportedOperationException("raiseTo a number biger than Int32.MAX is not support yet");
        }
    }
    
    public Real raiseTo( Real other){
        if (this.isZero()){
            if (other.isZero()){
                return Rational.ONE;
            }
            return Rational.ZERO;
        } else if (this.isOne()){
            return Rational.ONE;
        } else if (other.isZero()){
            return Rational.ONE;
        } else if (other.isOne()){
            return Rational.constructor(this);
        } 
        return new BigDecimal(this.value).raiseTo(other);
    }

    @Override
    public boolean isInInt32Range() {
        return this.value.longValue() <= java.lang.Integer.MAX_VALUE;
    }

  
    public Natural remainder(Natural other) {
        return new BigNatural(this.value.remainder(other.asJavaBigInteger()));
    }

    @Override
    public Natural wholeDivide(Natural other) {
        return new BigNatural(this.value.divide(other.asJavaBigInteger()));
    }

	@Override
	public boolean isPositive() {
		return this.value.signum() > 0;
	}



	@Override
	public Natural wrapMinus(Natural other) {
		BigInteger rest = this.value.subtract(other.asJavaBigInteger());
		if (rest.signum() <=0) {
			return Natural64.ZERO;
		}
	    return new BigNatural(rest);
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
	
	public boolean equalsTo(Any other) {
		return this.compareWith(other).nativeValue() == 0;
	}

	@Override
	public Whole plus(Whole other) {
		return new BigInt(this.value.add(new BigInteger(other.toString())));
	}

}
