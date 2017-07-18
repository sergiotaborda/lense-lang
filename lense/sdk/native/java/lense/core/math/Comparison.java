package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.java.Constructor;

public abstract class Comparison implements Any {

	@Constructor // TODO remove
	public static Comparison constructor (){
		return Equal.constructor();
	}
	
	public static Comparison valueOfNative(int compareTo) {
		 if (compareTo < 0){
			return Smaller.constructor();
		} else if (compareTo > 0){
			return Greater.constructor();
		} else {
			return Equal.constructor();
		}
	}

}
