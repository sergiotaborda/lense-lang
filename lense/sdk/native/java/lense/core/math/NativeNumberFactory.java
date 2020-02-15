package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.java.Primitives;
import lense.core.lang.reflection.Type;
import lense.core.lang.reflection.TypeResolver;

@PlatformSpecific
public final class NativeNumberFactory {

	public static final TypeResolver NATURAL_TYPE_RESOLVER = TypeResolver.lazy(() -> new Type(Natural.class));
	

    public static int naturalToPrimitiveInt(Natural natural) {
        if (natural instanceof Natural64) {
            return ((Natural64) natural).toPrimitiveInt();
        } else if (natural instanceof BigNatural) {
            return ((BigNatural) natural).toPrimitiveInt();
        } else {
            return new BigNatural(new BigInteger(natural.toString())).toPrimitiveInt();
        }
    }

    public static Natural naturalZero() {
        return Natural64.ZERO;
    }

    public static Natural naturalOne() {
        return Natural64.ONE;
    }

    public static Natural newNatural(long nativeValue) {
        if (nativeValue < 0) {
            throw ArithmeticException.constructor(
                    lense.core.lang.String.valueOfNative("A negative integer cannot be transformed to a Natural"));
        }
        return new Natural64(nativeValue);
    }

    public static Natural newNatural(String nativeValue) {
        return newNatural(new BigInteger(nativeValue));
    }

    public static Natural newNatural(BigInteger n) {
        if (n.signum() < 0) {
            throw ArithmeticException.constructor(
                    lense.core.lang.String.valueOfNative("A negative integer cannot be transformed to a Natural"));
        }
        if (n.compareTo(new BigInteger("18446744073709551615")) <= 0) {
            return new Natural64(n.toString());
        }
        return new BigNatural(n);
    }

    public static Imaginary newImaginary(long nativeValue) {
        return ImaginaryOverReal.valueOf(Rational.valueOf(newInteger(nativeValue)));
    }

    public static Integer newInteger(long nativeValue) {
        return Int64.valueOfNative(nativeValue);
    }

    public static Integer newInteger(int nativeValue) {
        return Int32.valueOfNative(nativeValue);
    }

    public static Real newReal(String nativeValue) {
        return BigDecimal.valueOfNative(nativeValue);
    }

    
    public static Float newFloat(String nativeValue) {
        return Float64.valueOfNative(Double.parseDouble(nativeValue));
    }
    
    public static BigFloat newBigFloat(String nativeValue) {
        return BigFloat.parse(lense.core.lang.String.valueOfNative(nativeValue));
    }
    
    public static Imaginary newImaginary(String nativeValue) {
        return ImaginaryOverReal.valueOf(newReal(nativeValue));
    }

    public static Integer newInteger(String nativeValue) {
        return new BigInt(new BigInteger(nativeValue));
    }

    public static Int32 newInt32(String nativeValue) {
        return Int32.valueOfNative(java.lang.Integer.parseInt(nativeValue));
    }

    public static Int32 newInt32(int nativeValue) {
        return Int32.valueOfNative(nativeValue);
    }

    public static Int64 newInt64(long nativeValue) {
        return Int64.valueOfNative(nativeValue);
    }

    public static Int64 newInt64(String nativeValue) {
        return Int64.valueOfNative(java.lang.Long.parseLong(nativeValue));
    }

    public static Float64 newFloat64(String nativeValue) {
        return Float64.valueOfNative(java.lang.Double.parseDouble(nativeValue));
    }

    public static Float32 newFloat32(String nativeValue) {
        return Float32.valueOfNative(java.lang.Float.parseFloat(nativeValue));
    }

    public static Integer plusAndCreateInteger(int a, int b) {
        try {
            return newInt32(Math.addExact(a, b));
        } catch (ArithmeticException e) {
            return newInt64(a).plus(newInt64(b));
        }
    }

    public static Integer plusAndCreateInteger(long a, long b) {
        try {
            return newInt64(Math.addExact(a, b));
        } catch (ArithmeticException e) {
            return Int64.valueOfNative(a).plus(Int64.valueOfNative(b));
        }
    }

    public static Integer minusAndCreateInteger(int a, int b) {
        try {
            return newInt32(Math.subtractExact(a, b));
        } catch (ArithmeticException e) {
            return newInt64(a).plus(newInt64(b));
        }
    }

    public static Integer minusAndCreateInteger(long a, long b) {
        try {
            return newInt64(Math.subtractExact(a, b));
        } catch (ArithmeticException e) {
            return Int64.valueOfNative(a).plus(Int64.valueOfNative(b));
        }
    }

    public static Integer multiplyAndCreateInteger(int a, int b) {
        try {
            return newInt32(Math.multiplyExact(a, b));
        } catch (ArithmeticException e) {
            return newInt64(a).plus(newInt64(b));
        }
    }

    public static Integer multiplyAndCreateInteger(long a, long b) {
        try {
            return newInt64(Math.multiplyExact(a, b));
        } catch (ArithmeticException e) {
            return Int64.valueOfNative(a).plus(Int64.valueOfNative(b));
        }
    }

    public static Comparison compareNumbers(RealLineElement a, RealLineElement b) {

    	if (a.getClass().isInstance(b)) {
    		// if they are the same class. All classes compare to them selfs
    		return a.compareWith(b);
    	}
    	
        return compareFloat(a.asFloat(), b.asFloat());
    }

    public static Comparison compareFloat(Float a , Float b) {
    	
    	if (a.getClass().isInstance(b)) {
    		// if they are the same class. All classes compare to them selfs
    		return a.compareWith(b);
    	}
    	
		if (a.isNaN()) {
			if (b.isNaN()) {
				return Equal.EQUAL;
			} 
				
			return Greater.GREATER;
			
		} else if (a.isNegativeInfinity()) {
			if (b.isNegativeInfinity()) {
				return Equal.EQUAL;
			} 
				
			return Smaller.SMALLER;
			
		} else if (a.isPositiveInfinity()) {
			if (b.isPositiveInfinity()) {
				return Equal.EQUAL;
			} 
				
			return Smaller.SMALLER;
			
		} else if (a.isNegativeZero()) {
			if (b.isNegativeZero()) {
				return Equal.EQUAL;
			} else if (b.isNegative()){
				return  Greater.GREATER;
			}
			return Smaller.SMALLER;
		} else if (a.isZero()) {
			if (b.isZero()) {
				return Equal.EQUAL;
			} else if (b.isNegativeZero()) {
				return Greater.GREATER;
			} else if (b.isNegative()){
				return  Greater.GREATER;
			}
			return Smaller.SMALLER;
		}
		
		return BigFloat.valueOf(a).compareWith(BigFloat.valueOf(b));
	}
    
	public static Comparison compareFloat(Float a , RealLineElement b) {
		if (b instanceof Float) {
			return compareFloat(a, (Float)b);
		}
		
		if (a.isNaN() || a.isPositiveInfinity()) {
			return Greater.GREATER;
			
		} else if (a.isNegativeInfinity()) {
	
			return Smaller.SMALLER;

		} else if (a.isNegativeZero()) {
			if (b.isNegative()){
				return  Greater.GREATER;
			}
			return Smaller.SMALLER;
		} 
		
		return compareFloat(a, b.asFloat());
	}
    
    public static int toPrimitiveInt(Whole number){
        if (number instanceof Int32){
            return ((Int32) number).value;
        }
        try {
            return new BigInteger(number.toString()).intValueExact();
        } catch (ArithmeticException e){
            throw lense.core.math.ArithmeticException.constructor(e);
        }
    }

    public static long toPrimitiveLong(Whole number){
        if (number instanceof Int32){
            return ((Int32) number).value;
        } else if (number instanceof Int64){
            return ((Int64) number).value;
        }
        try {
            return new BigInteger(number.toString()).longValueExact();
        } catch (ArithmeticException e){
            throw lense.core.math.ArithmeticException.constructor(e);
        }
    }
}
