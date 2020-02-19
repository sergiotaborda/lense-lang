package lense.core.math;

public interface Complex extends Number {


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
