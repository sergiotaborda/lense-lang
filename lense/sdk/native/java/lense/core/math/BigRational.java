package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.AnyValue;
import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.NonNull;
import lense.core.lang.java.Primitives;
import lense.core.lang.java.Signature;
import lense.core.lang.java.ValueClass;
import lense.core.lang.reflection.Type;

@Signature("::lense.core.math.Real")
@ValueClass 
public final class BigRational implements Rational , AnyValue  {

	
	public static final BigRational ZERO = new BigRational(Int32.ZERO, Int32.ONE);
	public static final BigRational ONE = new BigRational(Int32.ONE, Int32.ONE);
	
	@Constructor(paramsSignature = "")
    public static BigRational valueOf(Whole n){
        return new BigRational(n.asInteger(), Int32.ONE);
    }
	
	@Constructor(paramsSignature = "")
    public static BigRational constructor(Integer n , Integer d){
        return new BigRational(n,d);
    }
	
	@lense.core.lang.java.Constructor(paramsSignature="")
	public  static lense.core.math.BigRational zero(){
	    return ZERO;
	}

	@lense.core.lang.java.Constructor(paramsSignature="")
	public  static lense.core.math.BigRational one(){
	    return ONE;
	}


    private Integer numerator;
    private Natural denominator;

    private BigRational(@NonNull Integer n, @NonNull Integer d) {
        numerator = n.multiply(d.sign());
        denominator = d.abs();
    }
    
    private BigRational(@NonNull Integer n, @NonNull Natural d) {
        numerator = n;
        denominator = d;
    }

    public Integer getNumerator(){
        return numerator;
    }

    public Natural getDenominator(){
        return denominator;
    }


    public boolean equalsTo(BigRational other) {
        return this.numerator.compareWith(other.numerator).isEqual() && this.denominator.compareWith(other.denominator).isEqual();
    }


    public lense.core.lang.String asString(){
        if (this.denominator.isOne()){
            return this.numerator.asString();
        } 
        return this.numerator.asString().concat("/").concat(this.denominator.toString());
    }

    private BigRational symplify(Integer numerator, Natural denominator) {
        return new BigRational(numerator,denominator );
    }

    @Override
    public Real plus(Real other) {
        if (other instanceof BigRational){
            return plus((BigRational)other);
        } else {
            return promoteNext().plus(other);
        }
    }


    public Rational plus(Rational other) {
        return symplify(
                this.denominator.multiply(other.getNumerator()).plus(other.getDenominator().multiply(this.numerator)) , 
                this.denominator.multiply(other.getDenominator()) 
                );
    }

    @Override
    public Real minus(Real other) {
        if (other instanceof BigRational){
            return minus((BigRational)other);
        } else {
            return promoteNext().minus(other);
        }
    }

    public Rational minus(Rational other) {
        return symplify(
                this.denominator.multiply(other.getNumerator()).minus(this.denominator.multiply(other.getDenominator())) , 
                this.denominator.multiply(other.getDenominator()) 
                );
    }

    @Override
    public Real multiply(Real other) {
        if (other instanceof BigRational){
            return multiply((BigRational)other);
        } else {
            return promoteNext().multiply(other);
        }
    }

    public Rational multiply(Rational other) {
        return symplify(
                this.numerator.multiply(other.getNumerator()),
                this.denominator.multiply(other.getDenominator())
                );
    }

    @Override
    public Real divide(Real other) {
        if (other instanceof BigRational){
            return divide((BigRational)other);
        } else {
            return promoteNext().divide(other);
        }
    }

    public Rational divide(Rational other) {
        if (other.isZero()){
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
        }
        return symplify(
                other.getDenominator().multiply(this.numerator),
                this.denominator.multiply(other.getNumerator().abs())
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
        return new BigRational(numerator.symmetric(), denominator);
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
                return BigRational.ONE;
            }
            return this;
        }
        if (other.isWhole()){
            Integer p = other.floor();
            Natural n = p.abs();
            Rational result = new BigRational(this.numerator.raiseTo(n), this.denominator.raiseTo(n));
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

	public Rational invert() {
        if (this.isZero()){
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot invert zero"));
        }
        return new BigRational(this.denominator.multiply(Int32.ONE), this.numerator.abs());
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
        return new BigRational(this.numerator.abs().asInteger(), this.denominator.abs().asInteger());
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
		if (other instanceof BigRational) {
			return this.numerator.multiply(((BigRational) other).numerator).minus(((BigRational)other).numerator.multiply( this.numerator)).sign().compareWith(Int32.ZERO);
		} else if (other instanceof Number && other instanceof Comparable) {
			return Primitives.comparisonFromNative(NativeNumberFactory.compareNumbers(this, (Number) other));
		} 
		throw new ClassCastException("Cannot compare Rational to " + other.getClass().getName());
	}
	
	@Override
	public Type type() {
		return Type.fromName(this.getClass().getName());
	}
	
	@Override
	public Integer asInteger() {
		return this.floor();
	}
	


	@Override
	public Complex plus(Imaginary other) {
		return Complex.retangular(this, other.real());
	}

	@Override
	public Complex minus(Imaginary other) {
		return Complex.retangular(this, other.real().symmetric());
	}
	

	@Override
	public Imaginary multiply(Imaginary other) {
		return Imaginary.valueOf(this.multiply(other.real()));
	}
	

	@Override
	public Imaginary divide(Imaginary other) {
		return Imaginary.valueOf(this.divide(other.real()));
	}


}
