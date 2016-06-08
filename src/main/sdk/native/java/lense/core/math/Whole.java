package lense.core.math;

import lense.core.lang.java.Constructor;

public class Whole extends Number {

	@Constructor
	public static Whole constructor(){
		return Int32.valueOfNative(0);
	}
}
