package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Signature;
import lense.core.lang.java.ValueClass;
import lense.core.lang.reflection.Type;

@Signature("::lense.core.math.Number")
@ValueClass
public final class Complex implements Number {


    @Constructor(paramsSignature = "lense.core.math.Real")
    public static Complex valueOfReal( Real real){
        return new Complex(real, Rational.zero());
    }
    
    @Constructor(paramsSignature = "lense.core.math.Real, lense.core.math.Real")
    private static  Complex constructor (Real real, Real imaginary){
        return new Complex(real, imaginary);
    }
    
    public static  Complex retangular (Real real, Real imaginary){
        return new Complex(real, imaginary);
    }

    private Real real;
    private Real imginary;

    private Complex(Real real, Real imginary){
        this.real = real;
        this.imginary = imginary;
    }

    public Complex plus(Complex other) {
    	if (other instanceof Complex) {
    		
    	}
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
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
        }
        return this.multiply(other.conjugate()).divide(denominator);
    }

    public Complex divide(Real denominator) {
        return new Complex(this.real.divide(denominator), this.imginary.divide(denominator));
    }

    public Complex conjugate(){
        return new Complex(this.real, this.imginary.symmetric());
    }

    public Real abs(){
        return real.multiply(real).plus(this.imginary.multiply(this.imginary)).raiseTo(Rational.constructor(Int32.ONE, Int32.TWO));
    }

    @Override
    public boolean equalsTo(Any other) {
        return other instanceof Complex && equalsTo((Complex)other);
    }

    public boolean equalsTo(Complex other) {
        return this.real.equalsTo(other.real) && this.imginary.equalsTo(other.imginary);
    }

    @Override
    public HashValue hashValue() {
        return new HashValue(this.real.hashCode() ^ this.imginary.hashCode());
    } 


    public lense.core.lang.String asString(){
        return real.asString().concat(imginary.sign().isNegative() ? "-" : "+").concat(imginary.asString()).concat("i");
    }
    
    @Override
    public boolean isZero() {
        return this.real.isZero() && this.imginary.isZero();
    }

	@Override
	public Type type() {
		return Type.fromName(this.getClass().getName());
	}
	
	@Override
    public String toString(){
        return  this.real.toString() + "" + this.imginary.toString() + "i";
    }
    
    @Override
    public boolean equals(Object other){
        return other instanceof Any && equalsTo((Any)other);
    }
    
    @Override
    public int hashCode(){
        return hashValue().hashCode();
    }

}
