package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.String;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.SingletonObject;


@SingletonObject
public final class Smaller extends Comparison{

	public static Smaller SMALLER = new Smaller();

	@Constructor(paramsSignature = "")
	public static Smaller constructor(){
		return SMALLER;
	}

	@Override
	public boolean equalsTo(Any other) {
		return other instanceof Smaller;
	}

	@Override
	public HashValue hashValue() {
		return new HashValue(-1);
	}
	
	@Override
	public String asString() {
		return String.valueOfNative("SMALLLER");
	}


	@Override
	public boolean isSmaller() {
		return true;
	}

	@Override
	public boolean isEqual() {
		return false;
	}

	@Override
	public boolean isGreater() {
		return false;
	}
}
