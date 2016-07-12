package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.SingletonObject;

@SingletonObject
public final class Greater extends Comparison{

	public static Greater Greater = new Greater();

	@Constructor
	public static Greater constructor(){
		return Greater;
	}

	@Override
	public Boolean equalsTo(Any other) {
		return Boolean.valueOfNative(other == Greater);
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(1);
	}
}
