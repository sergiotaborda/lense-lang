package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.IllegalIndexException;

public class BooleanArrayIterator implements Iterator {

	private boolean[] array;
	private int index =-1;

	public BooleanArrayIterator(boolean[] array){
		this.array = array;
	}
	
	@Override
	public boolean hasNext() {
		return index < array.length - 1;
	}

	@Override
	public Any next() {
		index++;
		if (index >= array.length){
			throw IllegalIndexException.constructor();
		}
		return Boolean.valueOfNative(array[index]);
	}

}
