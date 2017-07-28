package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.java.Constructor;

public class BigDecimal extends Decimal {

    @Constructor
    public static BigDecimal constructor (){
        return new BigDecimal(java.math.BigDecimal.ZERO);
    }

    @Constructor
    public static BigDecimal constructor (Rational other){
        return new BigDecimal(other.getNumerator().asBigInteger().divide(other.getDenominator().asBigInteger()) );
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
    public Integer hashValue() {
        return Int32.valueOfNative(this.value.hashCode());
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
    public Real symetric() {
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
}
