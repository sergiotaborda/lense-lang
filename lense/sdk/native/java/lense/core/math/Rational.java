package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;

public class Rational extends Real{

    @Constructor
    public static Rational constructor(Integer n , Integer d){
        return new Rational(n,d);
    }


    private Integer numerator;
    private Integer denominator;

    private Rational(Integer n, Integer d) {
        numerator = n;
        denominator = d;
    }

    public Integer getNumerator(){
        return numerator;
    }

    public Integer getDenominator(){
        return denominator;
    }

    public Int32 compareTo(Real other){
        return super.compareTo(other);
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

    private Rational symplify(Integer numerator, Integer denominator) {
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
                this.numerator.multiply(other.denominator).plus(this.denominator.multiply(other.numerator)) , 
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
                this.numerator.multiply(other.denominator).minus(this.denominator.multiply(other.numerator)) , 
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
                this.numerator.multiply(other.denominator),
                this.denominator.multiply(other.numerator)
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
        return new BigDecimal(new java.math.BigDecimal(numerator.asBigInteger().divide(this.denominator.asBigInteger()).toString()));
    }
}
