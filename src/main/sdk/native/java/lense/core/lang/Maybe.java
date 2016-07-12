package lense.core.lang;

import lense.core.lang.java.Constructor;
import lense.core.math.Int32;
import lense.core.math.Integer;

public class Maybe implements Any{

	@Constructor
	public static Maybe none(){
		return new Maybe(); // TODO
	}
	
	@Override
	public Boolean equalsTo(Any other) {
		return Boolean.valueOfNative(false);
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(0);
	}
}
