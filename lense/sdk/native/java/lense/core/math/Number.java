package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;

public abstract class Number extends Base implements Any{

	@Constructor
	public static Number constructor (){
		throw new IllegalArgumentException("");
	}

	public abstract boolean isZero();
}
