package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.java.Native;
import lense.core.math.Natural;

@Native
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
	public boolean moveNext() {
		if (!current.equalsTo(last)){
			current = current.successor();
			return true;
		}
		return false;
	}

	@Override
	public Any current() {		
		return current;
	}
	
}