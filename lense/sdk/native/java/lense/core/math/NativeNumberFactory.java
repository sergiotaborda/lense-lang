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
    
    public static Int32 newInt32(int nativeValue){
        return Int32.valueOfNative(nativeValue);
    }
    
    public static Int64 newInt64(long nativeValue){
        return Int64.valueOfNative(nativeValue);
    }
    
    public static Decimal32 newDecimal32(float nativeValue){
        return Decimal32.valueOfNative(nativeValue);
    }
    public static Decimal64 newDecimal64(double nativeValue){
        return Decimal64.valueOfNative(nativeValue);
    }
    
    public static Integer plusAndCreateInteger(int a, int b){
        try {
            return newInt32(Math.addExact(a, b));
        } catch (ArithmeticException e){
            return newInt64(a).plus(newInt64(b));
        }
    }
    
    public static Integer plusAndCreateInteger(long a, long b){
        try {
            return newInt64(Math.addExact(a, b));
        } catch (ArithmeticException e){
            return BigInt.valueOfNative(a).plus(BigInt.valueOfNative(b));
        }
    }
    
    
    public static Integer minusAndCreateInteger(int a, int b){
        try {
            return newInt32(Math.subtractExact(a, b));
        } catch (ArithmeticException e){
            return newInt64(a).plus(newInt64(b));
        }
    }
    
    public static Integer minusAndCreateInteger(long a, long b){
        try {
            return newInt64(Math.subtractExact(a, b));
        } catch (ArithmeticException e){
            return BigInt.valueOfNative(a).plus(BigInt.valueOfNative(b));
        }
    }
    
    public static Integer multiplyAndCreateInteger(int a, int b){
        try {
            return newInt32(Math.multiplyExact(a, b));
        } catch (ArithmeticException e){
            return newInt64(a).plus(newInt64(b));
        }
    }
    
    public static Integer multiplyAndCreateInteger(long a, long b){
        try {
            return newInt64(Math.multiplyExact(a, b));
        } catch (ArithmeticException e){
            return BigInt.valueOfNative(a).plus(BigInt.valueOfNative(b));
        }
    }
}
