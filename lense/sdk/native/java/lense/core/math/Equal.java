package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.String;
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
	public boolean equalsTo(Any other) {
		return other instanceof Equal;
	}

	@Override
	public HashValue hashValue() {
		return new HashValue(0);
	}

	@Override
	public String asString() {
		return String.valueOfNative("EQUAL");
	}
}
