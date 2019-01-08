package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.String;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.SingletonObject;

@SingletonObject
public final class Greater extends Comparison{

	public static Greater GREATER = new Greater();

	@Constructor(paramsSignature = "")
	public static Greater constructor(){
		return GREATER;
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


	@Override
	public boolean isSmaller() {
		return false;
	}

	@Override
	public boolean isEqual() {
		return false;
	}

	@Override
	public boolean isGreater() {
		return true;
	}
}
