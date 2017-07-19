package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.java.Constructor;

public class Complex extends Number{

	@Constructor
	public static Complex constructor (){
		return new Complex(Real.Zero, Real.Zero);
	}
	
	private Real real;
	private Real imginary;

	Complex(Real real, Real imginary){
		 this.real = real;
		 this.imginary = imginary;
	 }
	
	public Complex plus(Complex other) {
		return new Complex(this.real.plus(other.real), this.imginary.plus(other.imginary));
	}

	public Complex minus(Complex other) {
		return new Complex(this.real. minus(other.real), this.imginary. minus(other.imginary));
	}

	public Complex multiply(Complex other) {
		return new Complex(
				this.real.multiply(other.real).minus(this.imginary.multiply(other.imginary)) ,
				this.real.multiply(other.imginary).plus(this.imginary.multiply(other.real))
		);
	}

	public Complex divide(Complex other) {
		Real denominator =  other.abs();
		if (denominator.isZero()){
			throw new ArithmeticException();
		}
		return this.multiply(other.conjugate()).divide(denominator);
	}
	
	public Complex divide(Real denominator) {
		return new Complex(this.real.divide(denominator), this.imginary.divide(denominator));
	}

	public Complex conjugate(){
		return new Complex(this.real, this.imginary.symetric());
	}
	
	public Real abs(){
		return real.multiply(real).plus(this.imginary.multiply(this.imginary));
	}
	
	@Override
	public boolean equalsTo(Any other) {
		return other instanceof Complex && equals((Complex)other);
	}
	
	public boolean equalsTo(Complex other) {
		return this.real.equalsTo(other.real) && this.imginary.equalsTo(other.imginary);
	}

	@Override
	public Integer hashValue() {
		return Integer.valueOfNative(this.real.hashCode() ^ this.imginary.hashCode());
	} 
	
	
	public lense.core.lang.String asString(){
		return real.asString().plus(imginary.signum().compareTo(Integer.ZERO) <=0 ? "-" : "+").plus(imginary.asString()).plus("i");
	}
}