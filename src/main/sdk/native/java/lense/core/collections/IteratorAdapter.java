package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.Boolean;

public class IteratorAdapter implements Iterator{

	
	private java.util.Iterator<? extends Any> original;

	public IteratorAdapter (java.util.Iterator<? extends Any> original){
		this.original = original;
	}
	
	@Override
	public Boolean hasNext() {
		return Boolean.valueOfNative(original.hasNext());
	}

	@Override
	public Any next() {
		return original.next();
	}

}
