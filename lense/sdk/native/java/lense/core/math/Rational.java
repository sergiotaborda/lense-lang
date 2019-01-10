package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.NonNull;
import lense.core.lang.java.Primitives;
import lense.core.lang.java.Signature;
import lense.core.lang.java.ValueClass;
import lense.core.lang.reflection.Type;

@Signature("::lense.core.math.Real")
@ValueClass 
public final class Rational implements Real  {

	
	private static final Rational ZERO = new Rational(Int32.ZERO, Int32.ONE);
	private static final Rational ONE = new Rational(Int32.ONE, Int32.ONE);
	
	@Constructor(paramsSignature = "")
    public static Rational valueOf(Whole n){
        return new Rational(n.asInteger(), Int32.ONE);
    }
	
	@Constructor(paramsSignature = "")
    public static Rational constructor(Integer n , Integer d){
        return new Rational(n,d);
    }
	
	@lense.core.lang.java.Constructor(paramsSignature="")
	public  static lense.core.math.Rational zero(){
	    return ZERO;
	}

	@lense.core.lang.java.Constructor(paramsSignature="")
	public  static lense.core.math.Rational one(){
	    return ONE;
	}


    private Integer numerator;
    private Natural denominator;

    private Rational(@NonNull Integer n, @NonNull Integer d) {
        numerator = n.multiply(d.sign());
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


    public boolean equalsTo(Rational other) {
        return this.numerator.compareWith(other.numerator).isEqual() && this.denominator.compareWith(other.denominator).isEqual();
    }


    public lense.core.lang.String asString(){
        if (this.denominator.isOne()){
            return this.numerator.asString();
        } 
        return this.numerator.asString().concat("/").concat(this.denominator.toString());
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
                this.denominator.multiply(other.numerator).plus(other.denominator.multiply(this.numerator)) , 
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
    public Integer sign() {
        return numerator.sign();
    }

    private Real promoteNext() {
        return promoteToBigDecimal();
    }

    @Override
    public Real raiseTo(Real other) {
        if (this.isZero()){
            if (other.isZero()){
                return Rational.ONE;
            }
            return this;
        }
        if (other.isWhole()){
            Integer p = other.floor();
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

    private BigDecimal promoteToBigDecimal() {
		return BigDecimal.constructor(this);
	}

	private Rational invert() {
        if (this.isZero()){
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot invert zero"));
        }
        return new Rational(this.denominator.multiply(Int32.ONE), this.numerator.abs());
    }

    public Integer floor() {
        return numerator.wholeDivide(denominator);
    }

    @Override
    public boolean isWhole() {
        return this.denominator.isOne();
    }

    @Override
    public Real abs() {
        return new Rational(this.numerator.abs().asInteger(), this.denominator.abs().asInteger());
    }



	@Override
	public Integer ceil() {
		// TODO Auto-generated method stub
		return null;
	}


    @Override
    public boolean isPositive() {
        return this.sign().isPositive();
    }

    @Override
    public boolean isNegative() {
        return this.sign().isNegative();
    }

	public String toString() {
		return numerator.toString() + "/" + denominator.toString();
	}

    @Override
    public final HashValue hashValue(){
        return new HashValue(numerator.hashCode() + 7 * denominator.hashCode());
    }
    
	public int hashCode() {
		return numerator.hashCode() + 7 * denominator.hashCode();
	}
	
	public boolean equals(Object other) {
		return other instanceof Any && equalsTo((Any)other);
	}
	
	public boolean equalsTo(Any other) {
		return this.compareWith(other).isEqual();
	}

	
    @Override
	public Comparison compareWith(Any other) {
		if (other instanceof Rational) {
			return this.numerator.multiply(((Rational) other).numerator).minus(((Rational)other).numerator.multiply( this.numerator)).sign().compareWith(Int32.ZERO);
		} else if (other instanceof Number && other instanceof Comparable) {
			return Primitives.comparisonFromNative(NativeNumberFactory.compareNumbers(this, (Number) other));
		} 
		throw new ClassCastException("Cannot compare Rational to " + other.getClass().getName());
	}
	

	@Override
	public boolean isNaN() {
		return false;
	}
	
	@Override
	public boolean isNegativeInfinity() {
		return false;
	}
	
	@Override
	public boolean isPositiveInfinity() {
		return false;
	}
	
	@Override
	public boolean isInfinity() {
		return false;
	}

    @Override
    public boolean isNegativeZero() {
        return false;
    }
    
	@Override
	public Type type() {
		return Type.fromName(this.getClass().getName());
	}
}
