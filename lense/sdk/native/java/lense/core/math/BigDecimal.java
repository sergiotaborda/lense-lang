package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;

public class BigDecimal extends Decimal {

    @Constructor
    public static BigDecimal constructor (){
        return new BigDecimal(java.math.BigDecimal.ZERO);
    }

    @Constructor
    public static BigDecimal constructor (Rational other){
     
        java.math.BigDecimal n = new java.math.BigDecimal( other.getNumerator().asBigInteger());
        java.math.BigDecimal d = new java.math.BigDecimal( other.getDenominator().asBigInteger());
        
        return new BigDecimal(n.divide(d));
    }

    final java.math.BigDecimal value;

    BigDecimal(java.math.BigInteger value){
        this(new java.math.BigDecimal(value.toString()));
    }

    BigDecimal(java.math.BigDecimal value){
        this.value = value;
    }


    public Int32 compareTo(Real other){
        return super.compareTo(other);
    }


    @Override
    public boolean equalsTo(Any other) {
        return other instanceof Int64 && ((BigDecimal)other).value.compareTo(this.value) == 0;
    }

    @Override
    public HashValue hashValue() {
        return new HashValue(this.value.hashCode());
    }

    @Override
    public Real plus(Real other) {
        return new BigDecimal(this.value.add(other.promoteToBigDecimal().value));
    }

    @Override
    public Real minus(Real other) {
        return new BigDecimal(this.value.subtract(other.promoteToBigDecimal().value));
    }

    @Override
    public Real multiply(Real other) {
        return new BigDecimal(this.value.multiply(other.promoteToBigDecimal().value));
    }

    @Override
    public Real divide(Real other) {
        return new BigDecimal(this.value.divide(other.promoteToBigDecimal().value));
    }

    @Override
    public boolean isZero() {
        return this.value.signum() == 0;
    }

    @Override
    public boolean isOne() {
        return this.value.equals(java.math.BigDecimal.ONE);
    }

    @Override
    protected Real promoteNext() {
        return this;
    }

    @Override
    public Real symmetric() {
        return new BigDecimal(this.value.negate());
    }

    @Override
    public Integer signum() {
        return new Int32(this.value.signum());
    }


    @Override
    protected BigDecimal promoteToBigDecimal() {
        return this;
    }

    @Override
    public Real raiseTo(Real other) {
        // TODO use bigdecimal arithmetic. use double for now
        return Decimal64.valueOf(this).raiseTo(other);
    }
    
    @Override
    public Integer asInteger() {
        return  Integer.valueOfNative(this.value.toBigInteger());
    }


    @Override
    public boolean isWhole() {
        return this.value.remainder(java.math.BigDecimal.ONE).compareTo(java.math.BigDecimal.ZERO) == 0;
    }
}
