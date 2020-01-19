package lense.core.math;

import lense.core.lang.java.Signature;

@Signature("::lense.core.math.Number")
public interface Imaginary extends SignedNumber , Comparable{


	public Imaginary plus(Imaginary other);

	public Imaginary minus(Imaginary other);

	public Real multiply(Imaginary other);

	public Real divide(Imaginary other);
	
	public Complex plus(Whole other);

	public Complex minus(Whole other);

	public Imaginary multiply(Whole other);

	public Imaginary divide(Whole other);
	
	public Complex plus(Real other);

	public Complex minus(Real other);

	public Imaginary multiply(Real other);

	public Imaginary divide(Real other);
	
	public lense.core.lang.String asString();
	
    public Real real();

}
