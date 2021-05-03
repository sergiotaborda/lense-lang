package lense.core.math;

import java.math.BigInteger;
import java.util.BitSet;

import lense.core.lang.Binary;
import lense.core.lang.java.PlatformSpecific;

@PlatformSpecific
public class NativeNumerics {

	
	public static int modulus (Natural value, int module) {
		if (value instanceof Natural64 natural) {
			return (int)java.lang.Long.remainderUnsigned(natural.value, module);
		} else if (value instanceof BigNatural bigNatural) {
			return bigNatural.value.remainder(BigInteger.valueOf(module)).intValue();
		} 
		
		return new BigInteger(value.toString()).remainder(BigInteger.valueOf(module)).intValue();
	}

	public static BitSet bitSetFromBinary(Binary binary, int size) {
	
		var bitSet = new BitSet(size);

		var iterator = Natural64.ONE.upToExclusive(min(binary.getBitsCount(), Natural64.valueOfNative(size))).getIterator();
		
		int index = 0;
		while(iterator.moveNext()) {
			if (binary.bitAt((Natural)iterator.current())) {
				bitSet.set(index);
			}
			index++;
		}
		
		return bitSet;
	}
	
	private static Natural min(Natural a, Natural b) {
		if (a.compareWith(b).isGreater()) {
			return b;
		}
		
		return a;
	}

	public static Float round(Float afloat) {
		if (afloat.isNaN() || afloat.isInfinity() || afloat.isNegativeZero()) {
			return afloat;
		}  
		
		return afloat.isNegative() ? afloat.ceil() : afloat.floor();
	}

	public static Float remainder(Float a, Float b) {
		if (a.isInfinity() && b.isInfinity() || a.isNaN() || b.isNaN()) {
			return Float64.valueOfNative(Double.NaN);
		} else if (b.isZero()) {
			return Float64.valueOfNative(Double.POSITIVE_INFINITY).multiply(a.sign().asFloat());
		} else if (b.isNegativeZero()) {
			return Float64.valueOfNative(Double.NEGATIVE_INFINITY).multiply(a.sign().asFloat());
		} else if (b.isOne()) {
			return a;
		} 
		
		return a.minus(b.multiply(a.divide(b).round()));
	}
	
	public static Float modulo(Float a, Float b) {
		if (a.isInfinity() && b.isInfinity() || a.isNaN() || b.isNaN()) {
			return Float64.valueOfNative(Double.NaN);
		} else if (b.isZero()) {
			return Float64.valueOfNative(Double.POSITIVE_INFINITY).multiply(a.sign().asFloat());
		} else if (b.isNegativeZero()) {
			return Float64.valueOfNative(Double.NEGATIVE_INFINITY).multiply(a.sign().asFloat());
		} else if (b.isOne()) {
			return a;
		} 
		
		return a.minus(b.multiply(a.divide(b).floor()));
	}

}
