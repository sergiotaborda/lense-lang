package lense.core.math;

import java.math.BigInteger;

public abstract class ScalableNatural extends Natural {

	protected abstract BigInteger asBigInteger();
	
	
	protected abstract Natural  promoteNext();


	protected abstract int maxByteCount();
	
}
