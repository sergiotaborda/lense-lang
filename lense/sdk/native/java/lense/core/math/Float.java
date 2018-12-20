package lense.core.math;

public interface Float extends Real , Comparable, SignedNumber{


	public abstract Float abs();

	public abstract Float symmetric();
	public abstract Float wrapPlus (Float other);
	public abstract Float warpMinus (Float other);
	public abstract Float wrapMultiply(Float other);
	public abstract Float wrapDivide(Float other);
	
	public abstract Float raiseTo(Float other);


}
