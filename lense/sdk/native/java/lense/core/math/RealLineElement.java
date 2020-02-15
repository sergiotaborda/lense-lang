package lense.core.math;

import lense.core.lang.java.Signature;

@Signature("::lense.core.math.Number&lense.core.math.Comparable<lense.core.math.RealLineElement>")
public interface RealLineElement extends  Number, Comparable {

	public Float asFloat();
	public boolean isNegative();
	public boolean isPositive();

}
