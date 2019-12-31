package lense.core.math;

public interface Float extends  Number, Comparable, SignedNumber{


	public boolean isNaN();
	public boolean isNegativeInfinity();
	public boolean isPositiveInfinity();
	public boolean isInfinity();
	
	public Float abs();

	public Float symmetric();
	
	public boolean isOne();
	public boolean isZero();
	
    public boolean isWhole();
	
    public Integer floor();
    public Integer ceil();
    
	public Float warpPlus (Float other);
	public Float warpMinus (Float other);
	public Float wrapMultiply(Float other);
	public Float wrapDivide(Float other);
	
	public Float plus (Float other);
	public Float minus (Float other);
	public Float multiply(Float other);
	public Float divide(Float other);
	
	public Float raiseTo(Float other);
	



}
