package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.java.Signature;

@Signature("::lense.core.math.Number&lense.core.math.Comparable<lense.core.math.Whole>")
public interface Whole extends Number , Comparable {

    public Whole plus (Whole other);
    public Whole minus (Whole other);

    public Rational divide(Whole other);

    public Real asReal();

    public Integer asInteger();
    
    public Natural gcd(Whole other);
    
    public Whole successor();
    public Whole predecessor();

    public boolean isZero();
    public boolean isOne();


    @Override
    public boolean equalsTo(Any other);

    public Natural abs();

	public Complex plus(Imaginary n);
	public Complex minus(Imaginary n );
	public Imaginary multiply(Imaginary  n );
	public Imaginary divide(Imaginary  n );

	public Whole wholeDivide (Whole other); 
	public Whole remainder (Whole other); 	
}