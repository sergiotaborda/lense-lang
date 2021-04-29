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

}
