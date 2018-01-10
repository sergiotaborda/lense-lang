package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.IllegalIndexException;

public class ArrayIterator implements Iterator {

	private final int maxSize;
	private int index =-1;
	private SmallArray array;

	public ArrayIterator(SmallArray array, int maxSize){
		this.array = array;
		this.maxSize = maxSize;
	}
	
	@Override
	public boolean moveNext() {
		if (index < maxSize - 1){
			index++;
			return true;
		}
		return false;
	}

	@Override
	public Any current() {
		if (index < 0 || index >= maxSize){
			throw IllegalIndexException.constructor();
		}
		return array.getAtPrimitiveIndex(index);
	}

}
