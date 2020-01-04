package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.java.PlatformSpecific;

@PlatformSpecific
public final class NativeNumberFactory {

	
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
        return Imaginary.valueOf(BigRational.valueOf(newInteger(nativeValue)));
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

    public static Imaginary newImaginary(String nativeValue) {
        return Imaginary.valueOf(newReal(nativeValue));
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

    public static int compareNumbers(Number a, Number b) {

        java.math.BigDecimal ga;
        if (a instanceof Rational) {
            ga = lense.core.math.BigDecimal.constructor((Rational) a).value;
        } else {
            ga = a instanceof BigDecimalConvertable ? ((BigDecimalConvertable) a).toBigDecimal()
                    : new java.math.BigDecimal(a.toString());
        }

        java.math.BigDecimal gb;
        if (b instanceof Rational) {
            gb = lense.core.math.BigDecimal.constructor((Rational) b).value;
        } else {
            gb = b instanceof BigDecimalConvertable ? ((BigDecimalConvertable) b).toBigDecimal()
                    : new java.math.BigDecimal(b.toString());
        }

        return ga.compareTo(gb);
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
