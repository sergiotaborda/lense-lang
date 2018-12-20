package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.java.PlatformSpecific;

@PlatformSpecific
public final class NativeNumberFactory {

	public static Natural naturalZero() {
		return Natural64.ZERO;
	}
	
	public static Natural naturalOne() {
		return Natural64.ONE;
	}
	
    public static Natural newNatural(long nativeValue){
    	if (nativeValue < 0) {
    		throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("A negative integer cannot be transformed to a Natural"));
    	}
        return new Natural64(nativeValue);
    }
    
    public static Natural newNatural(String nativeValue){
        return newNatural(new BigInteger(nativeValue));
    }
    
    public static Natural newNatural(BigInteger n) {
        if (n.signum() < 0){
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("A negative integer cannot be transformed to a Natural"));
        }
        if (n.compareTo(new BigInteger("18446744073709551615")) <= 0){
            return new Natural64(n.toString());
        } 
        return new BigNatural(n);
    }

    public static Imaginary newImaginary(long nativeValue){
        return Imaginary.valueOf(Rational.constructor(newInteger(nativeValue)));
    }
    
    public static Integer newInteger(long nativeValue){
        return Int64.valueOfNative(nativeValue);
    }
    
    public static Integer newInteger(int nativeValue){
        return Int32.valueOfNative(nativeValue);
    }
    
    public static Imaginary newImaginary(String nativeValue){
        return Imaginary.valueOf(Rational.constructor(newInteger(nativeValue)));
    }
    
    public static Integer newInteger(String nativeValue){
        return new BigInt(new BigInteger(nativeValue));
    }
    

}
