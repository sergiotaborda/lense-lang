package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.Ordinal;
import lense.core.lang.java.PlatformSpecific;

@PlatformSpecific
class NativeOrdinalIterator implements Iterator{


	private Ordinal current;
	private Ordinal last;
	private Ordinal first;
	private boolean includeEnd;
	private boolean stop = false;
	
	NativeOrdinalIterator(NativeOrdinalProgression nativeOrdinalProgression, Ordinal first){
	    this.first = first;
	    this.includeEnd = nativeOrdinalProgression.includeEnd;
		this.last = nativeOrdinalProgression.end;
	}
	
	@Override
	public boolean moveNext() {
	    
	    if (stop){
	        return false;
	    }
	    
		if (current == null){
			current = first;
			return true;
		} 
		
		Ordinal next = (Ordinal)current.successor();
		if (!next.equalsTo(last)){
			current = next;
			return true;
		}
		
		stop = true;
		
		if (includeEnd){
		    current = next;
            return true;
		}
		return false;
	}

	@Override
	public Any current() {		
	    if (current == null){
	        throw new IllegalStateException("Cannot read current value before moving");
	    }
		return current;
	}
	
}