package lense.core.collections;

import lense.core.lang.Any;

public class IteratorAdapter implements Iterator{

	
	private java.util.Iterator<? extends Any> original;

	public IteratorAdapter (java.util.Iterator<? extends Any> original){
		this.original = original;
	}
	
	@Override
	public boolean hasNext() {
		return original.hasNext();
	}

	@Override
	public Any next() {
		return original.next();
	}

}
