package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.SingletonObject;

@SingletonObject
public final class Equal extends Comparison{

	public static Equal Equal = new Equal();

	@Constructor
	public static Equal constructor(){
		return Equal;
	}

	@Override
	public Boolean equalsTo(Any other) {
		return Boolean.valueOfNative(other == Equal);
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(0);
	}
}
