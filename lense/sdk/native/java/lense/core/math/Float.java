package lense.core.math;

public interface Float extends  Number, Comparable, SignedNumber {


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
    
	public Float wrapPlus (Float other);
	public Float wrapMinus (Float other);
	public Float wrapMultiply(Float other);
	public Float divide(Float other);

	public Float raiseTo(Float other);
	public Float log();
	public Float exp();


}
