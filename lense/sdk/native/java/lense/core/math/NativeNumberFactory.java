package lense.core.math;

import lense.core.lang.java.PlatformSpecific;

@PlatformSpecific
public final class NativeNumberFactory {

    public static Natural newNatural(long nativeValue){
        return Natural.valueOfNative(nativeValue);
    }
    
    public static Imaginary newImaginary(long nativeValue){
        return Imaginary.valueOf(Real.valueOf(newInteger(nativeValue)));
    }
    
    public static Integer newInteger(long nativeValue){
        return Integer.valueOfNative(nativeValue);
    }
}
