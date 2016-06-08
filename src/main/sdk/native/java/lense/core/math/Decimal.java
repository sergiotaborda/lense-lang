package lense.core.math;

import lense.core.lang.java.Constructor;

public abstract class Decimal extends Real {

	@Constructor
	public static Decimal constructor (){
		return Decimal32.constructor();
	}
	

}
