package lense.core.math;

import lense.core.lang.java.Signature;

@Signature("::lense.core.math.RealLineElement&lense.core.math.SignedNumber")
public interface Float extends RealLineElement, SignedNumber {


	public boolean isNaN();
	public boolean isNegativeZero();
	public boolean isNegativeInfinity();
	public boolean isPositiveInfinity();
	public boolean isInfinity();
	
	public Float abs();

	public Float symmetric();
	
	public boolean isOne();
	public boolean isZero();
	
    public boolean isWhole();
    
    public Float invert();
    
    public Integer floor();
    public Integer ceil();
    
	public Float plus (Float other);
	public Float minus (Float other);
	public Float multiply(Float other);
	public Float divide(Float other);

	public Float raiseTo(Whole other);
	public Float raiseTo(Float other);
	public Float log();
	public Float exp();


}
