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
	private Natural first;

	NativeIterator(NativeNaturalProgression nativeNaturalProgression, Natural first){
		this.nativeNaturalProgression = nativeNaturalProgression;
		this.first = first;
		this.last = this.nativeNaturalProgression.end;
	}
	
	@Override
	public boolean moveNext() {
		if (current == null){
			current = first;
			return true;
		} else if (!current.equalsTo(last)){
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