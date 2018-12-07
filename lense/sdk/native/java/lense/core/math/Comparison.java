package lense.core.math;

import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.PlatformSpecific;

public abstract class Comparison extends Base  {

    @Constructor(paramsSignature = "")// TODO remove
	public static Comparison constructor (){
		return Equal.constructor();
	}
	
	@PlatformSpecific
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
