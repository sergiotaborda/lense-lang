package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Signature;
import lense.core.lang.java.ValueClass;
import lense.core.lang.reflection.Type;

@Signature("::lense.core.math.Number")
@ValueClass
public final class ImaginaryOverReal implements Imaginary {

	
	@Constructor(paramsSignature = "")
	public static ImaginaryOverReal constructor (){
		return valueOf(Rational.zero());
	}
	
	@Constructor(paramsSignature = "lense.core.math.Real")
	public static ImaginaryOverReal valueOf(Real real){
		return new ImaginaryOverReal(real);
	}

	private Real value;
	
	private ImaginaryOverReal(Real value) {
		this.value = value;
	}

	public Imaginary plus(Imaginary other) {
		return new ImaginaryOverReal(value.plus(other.real()));
	}

	public Imaginary minus(Imaginary other) {
		return new ImaginaryOverReal(value.minus(other.real()));
	}

	public Real multiply(Imaginary other) {
		return this.value.multiply(other.real()).symmetric();
	}

	public Real divide(Imaginary other) {
		return this.value.divide(other.real());
	}
	
	public Complex plus(Whole other) {
		return plus(Rational.valueOf(other));
	}

	public Complex minus(Whole other) {
		return minus(Rational.valueOf(other));
	}

	public Imaginary multiply(Whole other) {
		return multiply(Rational.valueOf(other));
	}

	public Imaginary divide(Whole other) {
		return divide(Rational.valueOf(other));
	}
	
	public Complex plus(Real other) {
		return ComplexOverReal.rectangular(other, value);
	}

	public Complex minus(Real other) {
		return ComplexOverReal.rectangular(value.symmetric(), value);
	}

	public Imaginary multiply(Real other) {
		return new ImaginaryOverReal(this.value.multiply(other));
	}

	public Imaginary divide(Real other) {
		return new ImaginaryOverReal(this.value.divide(other));
	}
	
	public lense.core.lang.String asString(){
		return this.value.asString().concat("i");
	}
	
	@Override
	public boolean equalsTo(Any other) {
		return other instanceof Imaginary && equalsTo((Imaginary)other);
	}

	public boolean equalsTo(Imaginary other) {
		return this.value.equalsTo(other.real());
	}
	
	@Override
	public HashValue hashValue() {
		return value.hashValue();
	}

    public Real real() {
        return value;
    }

    @Override
    public boolean isZero() {
        return value.isZero();
    }

	@Override
	public Type type() {
		return Type.fromName(this.getClass().getName());
	}

	
	@Override
	public Comparison compareWith(Any other) {
		return this.value.compareWith(((Imaginary)other).real());
	}

	@Override
	public boolean isNegative() {
		return this.value.isNegative();
	}

	@Override
	public boolean isPositive() {
		return this.value.isPositive();
	}

	@Override
	public Integer sign() {
		return this.value.sign();
	}
}
