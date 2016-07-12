package lense.core.collections;

import java.math.BigInteger;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.math.Natural;

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
	public Boolean hasNext() {
		return  Boolean.valueOfNative(current == null || current.add(this.nativeBigIntegerProgression.step).compareTo(this.nativeBigIntegerProgression.end) <= 0);
	}

	@Override
	public Any next() {
		if (current == null){
			current = start;
		} else {
			current = current.add(this.nativeBigIntegerProgression.step);
		}
		
		return Natural.valueOf(current);
	}
	
}