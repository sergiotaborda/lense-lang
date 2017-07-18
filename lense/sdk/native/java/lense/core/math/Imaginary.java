package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.java.Constructor;

public class Imaginary extends Number{

	
	@Constructor
	public static Imaginary constructor (){
		return Imaginary.valueOfNative(0);
	}
	
	public static Imaginary valueOfNative(long value){
		return Imaginary.valueOf(Rational.valueOfNative(value));
	}

	public static Imaginary valueOf(Real real){
		return new Imaginary(real);
	}

	Real value;
	
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
		return this.value.multiply(other.value).symetric();
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
		return new Complex(other, value);
	}

	public Complex minus(Real other) {
		return new Complex(value.symetric(), value);
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
	public Integer hashValue() {
		return value.hashValue();
	}
}
