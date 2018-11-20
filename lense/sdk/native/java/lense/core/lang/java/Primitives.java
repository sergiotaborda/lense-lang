package lense.core.lang.java;

import lense.core.lang.String;

@PlatformSpecific
public class Primitives {

    
    public static String asString(boolean value){
        return String.valueOfNative(java.lang.Boolean.toString(value));
    }
    
    public static String asString(long value){
        return String.valueOfNative(java.lang.Long.toString(value));
    }
    
    public static String asString(double value){
        return String.valueOfNative(java.lang.Double.toString(value));
    }
    
    public static String asString(char value){
        return String.valueOfNative(java.lang.Character.toString(value));
    }
}
