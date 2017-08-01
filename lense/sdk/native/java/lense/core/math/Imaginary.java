package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;

public class Imaginary extends Number{

	
	@Constructor
	public static Imaginary constructor (){
		return valueOf(Real.Zero);
	}
	
	@Constructor
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
		return plus(Real.valueOf(other));
	}

	public Complex minus(Whole other) {
		return minus(Real.valueOf(other));
	}

	public Imaginary multiply(Whole other) {
		return multiply(Real.valueOf(other));
	}

	public Imaginary divide(Whole other) {
		return divide(Real.valueOf(other));
	}
	
	public Complex plus(Real other) {
		return Complex.constructor(other, value);
	}

	public Complex minus(Real other) {
		return Complex.constructor(value.symmetric(), value);
	}

	public Imaginary multiply(Real other) {
		return new Imaginary(this.value.multiply(other));
	}

	public Imaginary divide(Real other) {
		return new Imaginary(this.value.divide(other));
	}
	
	public lense.core.lang.String asString(){
		return this.value.asString().plus("i");
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
}
