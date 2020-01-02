package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Signature;
import lense.core.lang.java.ValueClass;
import lense.core.lang.reflection.Type;

@Signature("::lense.core.math.Number")
@ValueClass
public final class Imaginary implements Number{

	
	@Constructor(paramsSignature = "")
	public static Imaginary constructor (){
		return valueOf(Rational.zero());
	}
	
	@Constructor(paramsSignature = "lense.core.math.Real")
	public static Imaginary valueOf(Real real){
		return new Imaginary(real);
	}

	private Real value;
	
	private Imaginary(Real value) {
		this.value = value;
	}

	public Imaginary plus(Imaginary other) {
		return new Imaginary(value.plus(other.value));
	}

	public Imaginary minus(Imaginary other) {
		return new Imaginary(value.minus(other.value));
	}

	public Real multiply(Imaginary other) {
		return this.value.multiply(other.value).symmetric();
	}

	public Real divide(Imaginary other) {
		return this.value.divide(other.value);
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
		return Complex.retangular(other, value);
	}

	public Complex minus(Real other) {
		return Complex.retangular(value.symmetric(), value);
	}

	public Imaginary multiply(Real other) {
		return new Imaginary(this.value.multiply(other));
	}

	public Imaginary divide(Real other) {
		return new Imaginary(this.value.divide(other));
	}
	
	public lense.core.lang.String asString(){
		return this.value.asString().concat("i");
	}
	
	@Override
	public boolean equalsTo(Any other) {
		return other instanceof Imaginary && equalsTo((Imaginary)other);
	}

	public boolean equalsTo(Imaginary other) {
		return this.value.equalsTo(other.value);
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
}
