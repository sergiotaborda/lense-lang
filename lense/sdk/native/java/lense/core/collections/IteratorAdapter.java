package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.IllegalIndexException;

public class IteratorAdapter implements Iterator{

	
	private final java.util.Iterator<? extends Any> original;
	private Any current = null;
	private boolean started = false;

	public IteratorAdapter (java.util.Iterator<? extends Any> original){
		this.original = original;
	}
	
	@Override
	public boolean moveNext() {
		if( original.hasNext()){
			started = true;
			current =  original.next();
		}
		return false;
	}

	@Override
	public Any current() {
		
		if (!started){
			throw IllegalIndexException.constructor();
		}
		return current;
	}

}
