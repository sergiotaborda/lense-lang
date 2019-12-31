package lense.core.math;

import lense.core.lang.java.Signature;


@Signature("::lense.core.math.Number&lense.core.math.Comparable<lense.core.math.Real>&lense.core.math.SignedNumber")
public interface Real extends Number, Comparable , SignedNumber {

	public Real abs();

	public Integer asInteger();
	
	public Real symmetric();
	
	public boolean isZero();
	public boolean isOne();
			
	public boolean isWhole();
	
	public Integer floor();
	
	public Integer ceil();
	
	public Real plus (Real other);
	public Real minus (Real other);
	public Real multiply(Real other);
	public Real divide(Real other);
	
	public Complex plus (Imaginary other);
	public Complex minus (Imaginary other);
	public Imaginary multiply(Imaginary other);
	public Imaginary divide(Imaginary other);
	
	public Real raiseTo(Real other);


}
