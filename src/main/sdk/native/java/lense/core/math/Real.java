package lense.core.math;

import java.math.BigDecimal;

import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;
import lense.core.lang.Boolean;

public abstract class Real extends Number{

	public static final Real Zero = Rational.valueOfNative(0);
	
	@Constructor
	public static Real constructor(){
		return Rational.constructor(Int32.valueOfNative(0), Int32.valueOfNative(1));
	}
	
	public Int32 compareTo(Real other){
		Real r = (Real)other;
		return Int32.valueOfNative(this.getNativeBig().compareTo(r.getNativeBig()));
	}

	@Native
	protected abstract BigDecimal getNativeBig();
	
	public abstract Real plus (Real other);
	public abstract Real minus (Real other);
	public abstract Real multiply(Real other);
	public abstract Real divide(Real other);
	
	public Complex plus (Imaginary other){
		return new Complex(this, other.value);	
	}
	
	public Complex minus (Imaginary other){
		return new Complex(this, other.value.symetric());
	}
	
	public Imaginary multiply(Imaginary other){
		return Imaginary.valueOf(this.multiply(other.value).symetric());
	}
	
	public Imaginary divide(Imaginary other){
		return Imaginary.valueOf(this.divide(other.value));
	}

	public static Real valueOf(Whole other) {
		return Rational.constructor(other.asInteger(), Integer.ONE);
	}

	public abstract Real symetric();

	public abstract boolean isZero();
	public abstract boolean isOne();

	public abstract Integer signum();
}