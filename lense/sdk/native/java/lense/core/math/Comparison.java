package lense.core.math;

import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;

public abstract class Comparison extends Base  {

    @Constructor(paramsSignature = "")// TODO remove
	public static Comparison constructor (){
		return Equal.constructor();
	}
	
	public abstract boolean isSmaller();
	public abstract boolean isEqual();
	public abstract boolean isGreater();
}
