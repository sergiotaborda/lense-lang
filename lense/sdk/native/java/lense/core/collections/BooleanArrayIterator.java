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
	public boolean moveNext() {
		if (index < array.length - 1){
			index++;
			return true;
		}
		return false;
	}

	@Override
	public Any current() {
		if (index < 0 || index >= array.length){
			throw IllegalIndexException.constructor();
		}
		return Boolean.valueOfNative(array[index]);
	}

}
