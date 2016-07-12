package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.SingletonObject;


@SingletonObject
public final class Smaller extends Comparison{

	public static Smaller Smaller = new Smaller();

	@Constructor
	public static Smaller constructor(){
		return Smaller;
	}

	@Override
	public Boolean equalsTo(Any other) {
		return Boolean.valueOfNative(other == Smaller);
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(-1);
	}
}
