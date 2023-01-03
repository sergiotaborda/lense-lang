package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.String;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.NativeString;
import lense.core.lang.java.SingletonObject;

@SingletonObject
public final class Equal extends Comparison {

	public static Equal EQUAL = new Equal();

	@Constructor(paramsSignature = "")
	public static Equal constructor(){
		return EQUAL;
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
		return NativeString.valueOfNative("EQUAL");
	}

	@Override
	public boolean isSmaller() {
		return false;
	}

	@Override
	public boolean isEqual() {
		return true;
	}

	@Override
	public boolean isGreater() {
		return false;
	}
}
