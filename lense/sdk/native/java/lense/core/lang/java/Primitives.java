package lense.core.lang.java;

import lense.core.lang.String;
import lense.core.math.Comparison;
import lense.core.math.Equal;
import lense.core.math.Greater;
import lense.core.math.Smaller;

@PlatformSpecific
public class Primitives {

	public static Comparison comparisonFromNative(int value) {
		if ( value == 0) {
			return Equal.EQUAL;
		} else if ( value > 0) {
			return Greater.GREATER;
		} else {
			return Smaller.SMALLER;
		}
	}

    
    public static String asString(boolean value){
        return NativeString.valueOfNative(java.lang.Boolean.toString(value));
    }
    
    public static String asString(long value){
        return NativeString.valueOfNative(java.lang.Long.toString(value));
    }
    
    public static String asString(double value){
        return NativeString.valueOfNative(java.lang.Double.toString(value));
    }
    
    public static String asString(char value){
        return NativeString.valueOfNative(java.lang.Character.toString(value));
    }
}
