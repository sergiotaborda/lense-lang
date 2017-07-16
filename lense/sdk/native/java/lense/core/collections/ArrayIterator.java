package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.IllegalIndexException;

public class ArrayIterator implements Iterator {

	private Any[] array;
	private int index =-1;

	public ArrayIterator(Any[] array){
		this.array = array;
	}
	
	@Override
	public boolean moveNext() {
		return ++index < array.length - 1;
	}

	@Override
	public Any current() {
		
		if (index < 0 || index >= array.length){
			throw IllegalIndexException.constructor();
		}
		return array[index];
	}

}
