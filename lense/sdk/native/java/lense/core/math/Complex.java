package lense.core.math;

import lense.core.lang.java.Property;

public interface Complex extends Number {


	@Property(name = "real")
	public Real getReal();

	@Property(name = "imaginary")
	public Real getImaginary();

	
    public Complex plus(Complex other);

    public Complex minus(Complex other);
    
    public Complex multiply(Complex other);
    
    public Complex divide(Complex other);

    public Complex divide(Real denominator);

    public Complex conjugate();

    public Real abs();

}
