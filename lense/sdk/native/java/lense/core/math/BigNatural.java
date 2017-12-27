package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.HashValue;

public class BigNatural extends Natural{

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

    protected BigInteger asJavaBigInteger(){
        return value;
    }

    public final HashValue hashValue(){
        return new HashValue(value.hashCode());
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
    protected Integer asInteger() {
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
                return Natural.ONE;
            }
            return this;
        } else if (this.isOne()){
            return this;
        } else if (other.isZero()){
            return Natural.ONE;
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
                return Real.ONE;
            }
            return Rational.ZERO;
        } else if (this.isOne()){
            return Rational.ONE;
        } else if (other.isZero()){
            return Real.ONE;
        } else if (other.isOne()){
            return Rational.constructor(this.asInteger(), Integer.ONE);
        } 
        return new BigDecimal(this.value).raiseTo(other);
    }

    @Override
    protected BigNatural asBigNat() {
        return this;
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



}
