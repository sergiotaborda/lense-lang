package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.java.Native;
import lense.core.lang.java.PlataformSpecific;
import lense.core.math.Natural;

@PlataformSpecific
class NativeIterator implements Iterator{

	/**
	 * 
	 */
	private final NativeNaturalProgression nativeNaturalProgression;
	private Natural current;
	private Natural last;
	
	NativeIterator(NativeNaturalProgression nativeNaturalProgression, Natural current){
		this.nativeNaturalProgression = nativeNaturalProgression;
		this.current = current;
		this.last = this.nativeNaturalProgression.end.successor();
	}
	
	@Override
	public boolean hasNext() {
		return !current.equalsTo(last);
	}

	@Override
	public Any next() {
		Any value = current;
		current = current.successor();
		return value;
	}
	
}