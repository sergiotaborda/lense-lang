package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.IllegalIndexException;
import lense.core.lang.java.PlataformSpecific;

@PlataformSpecific
public class ArrayIterator implements Iterator {

	private Any[] array;
	private int index =-1;

	public ArrayIterator(Any[] array){
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
		return array[index];
	}

}
