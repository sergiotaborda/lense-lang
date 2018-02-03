package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.NonNull;

public final class Rational extends Real {

	@Constructor(paramsSignature = "")
    public static Rational constructor(Integer n , Integer d){
        return new Rational(n,d);
    }


    private Integer numerator;
    private Natural denominator;

    private Rational(@NonNull Integer n, @NonNull Integer d) {
        numerator = n;
        denominator = d.abs();
    }
    
    private Rational(@NonNull Integer n, @NonNull Natural d) {
        numerator = n;
        denominator = d;
    }

    public Integer getNumerator(){
        return numerator;
    }

    public Natural getDenominator(){
        return denominator;
    }


    @Override
    public boolean equalsTo(Any other) {
        return other instanceof Rational &&  equalsTo((Rational)other);
    }

    public boolean equalsTo(Rational other) {
        return this.numerator.compareTo(other.numerator) == 0 && this.denominator.compareTo(other.denominator) == 0;
    }

    @Override
    public final HashValue hashValue(){
        return new HashValue(numerator.hashCode() ^ denominator.hashCode());
    }


    public lense.core.lang.String asString(){
        if (this.denominator.isOne()){
            return this.numerator.asString();
        } 
        return this.numerator.asString().plus("/").plus(this.denominator.toString());
    }

    private Rational symplify(Integer numerator, Natural denominator) {
        return new Rational(numerator,denominator );
    }

    @Override
    public Real plus(Real other) {
        if (other instanceof Rational){
            return plus((Rational)other);
        } else {
            return promoteNext().plus(other);
        }
    }


    public Rational plus(Rational other) {
        return symplify(
                this.denominator.multiply(other.numerator).plus(this.denominator.multiply(other.numerator)) , 
                this.denominator.multiply(other.denominator) 
                );
    }

    @Override
    public Real minus(Real other) {
        if (other instanceof Rational){
            return minus((Rational)other);
        } else {
            return promoteNext().minus(other);
        }
    }

    public Rational minus(Rational other) {
        return symplify(
                this.denominator.multiply(other.numerator).minus(this.denominator.multiply(other.numerator)) , 
                this.denominator.multiply(other.denominator) 
                );
    }

    @Override
    public Real multiply(Real other) {
        if (other instanceof Rational){
            return multiply((Rational)other);
        } else {
            return promoteNext().multiply(other);
        }
    }

    public Rational multiply(Rational other) {
        return symplify(
                this.numerator.multiply(other.numerator),
                this.denominator.multiply(other.denominator)
                );
    }

    @Override
    public Real divide(Real other) {
        if (other instanceof Rational){
            return divide((Rational)other);
        } else {
            return promoteNext().divide(other);
        }
    }

    public Rational divide(Rational other) {
        if (other.isZero()){
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
        }
        return symplify(
                other.denominator.multiply(this.numerator),
                this.denominator.multiply(other.numerator.abs())
                );
    }

    @Override
    public boolean isZero() {
        return this.numerator.isZero();
    }

    @Override
    public boolean isOne() {
        return this.numerator.equalsTo(denominator);
    }

    @Override
    public Real symmetric() {
        return new Rational(numerator.symmetric(), denominator);
    }

    @Override
    public Integer signum() {
        return numerator.signum();
    }

    private Real promoteNext() {
        return promoteToBigDecimal();
    }

    @Override
    protected BigDecimal promoteToBigDecimal() {
        return new BigDecimal(new java.math.BigDecimal(numerator.asJavaBigInteger().divide(this.denominator.asJavaBigInteger()).toString()));
    }

    @Override
    public Real raiseTo(Real other) {
        if (this.isZero()){
            if (other.isZero()){
                return Real.ONE;
            }
            return this;
        }
        if (other.isWhole()){
            Integer p = other.asInteger();
            Natural n = p.abs();
            Rational result = new Rational(this.numerator.raiseTo(n), this.denominator.raiseTo(n));
            if (p.isNegative()){
                result = result.invert();
            } 
            return result;
        } else {
            return promoteToBigDecimal().raiseTo(other);
        }
    }

    private Rational invert() {
        if (this.isZero()){
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot invert zero"));
        }
        return new Rational(this.denominator.multiply(Integer.ONE), this.numerator.abs());
    }

    @Override
    public Integer asInteger() {
        return promoteToBigDecimal().asInteger();
    }

    @Override
    public boolean isWhole() {
        return this.promoteToBigDecimal().isWhole();
    }

    @Override
    public Real abs() {
        return new Rational(this.numerator.abs().asInteger(), this.denominator.abs().asInteger());
    }

	





}
