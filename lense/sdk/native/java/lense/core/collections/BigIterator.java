package lense.core.collections;

import java.math.BigInteger;

import lense.core.lang.Any;
import lense.core.lang.java.PlatformSpecific;
import lense.core.math.NativeNumberFactory;
import lense.core.math.Natural;

@PlatformSpecific
public class BigIterator implements Iterator {
	
	/**
	 * 
	 */
	private final NativeBigIntegerProgression nativeBigIntegerProgression;
	private BigInteger current = null;
	private BigInteger start;
	
	public BigIterator(NativeBigIntegerProgression nativeBigIntegerProgression, BigInteger start) {
		this.nativeBigIntegerProgression = nativeBigIntegerProgression;
		this.start = start;
	}

	@Override
	public boolean moveNext() {
		if (current == null){
			current = start;
		} else {
			BigInteger newValue = current.add(this.nativeBigIntegerProgression.step);
			
			if(newValue.compareTo(this.nativeBigIntegerProgression.end) <= 0){
				current = newValue;
			} else {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public Any current() {
		return NativeNumberFactory.newNatural(current);
	}
	
}