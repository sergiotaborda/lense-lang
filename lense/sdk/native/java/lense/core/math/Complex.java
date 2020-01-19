package lense.core.math;

import lense.core.lang.java.Constructor;
import lense.core.lang.java.NotReplacedPlaceholderException;

public interface Complex extends Number {

	@Constructor(paramsSignature = "lense.core.math.Real, lense.core.math.Real")
	public static Complex rectangular(Real r, Real img){
		throw new NotReplacedPlaceholderException();
	}
	
	public Real getReal();

	public Real getImaginary();

	
    public Complex plus(Complex other);

    public Complex minus(Complex other);
    
    public Complex multiply(Complex other);
    
    public Complex divide(Complex other);

    public Complex divide(Real denominator);

    public Complex conjugate();

    public Real abs();

}
