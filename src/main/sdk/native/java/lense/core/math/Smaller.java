package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.String;
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
	public boolean equalsTo(Any other) {
		return other instanceof Smaller;
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(-1);
	}
	
	@Override
	public String asString() {
		return String.valueOfNative("SMALLLER");
	}
}
