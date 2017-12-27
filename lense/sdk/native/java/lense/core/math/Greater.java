package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.String;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.SingletonObject;

@SingletonObject
public final class Greater extends Comparison{

	public static Greater Greater = new Greater();

	@Constructor(paramsSignature = "")
	public static Greater constructor(){
		return Greater;
	}

	@Override
	public boolean equalsTo(Any other) {
		return other instanceof Greater;
	}

	@Override
	public HashValue hashValue() {
		return new HashValue(1);
	}
	
	@Override
	public String asString() {
		return String.valueOfNative("GREATER");
	}
}
