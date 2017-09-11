package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.IllegalIndexException;
import lense.core.lang.java.PlatformSpecific;

@PlatformSpecific
public class NativeArrayIterator implements Iterator {

	private Any[] array;
	private int index =-1;

	public NativeArrayIterator(Any[] array){
		this.array = array;
	}
	
	@Override
	public boolean moveNext() {
		return ++index < array.length;
	}

	@Override
	public Any current() {
		
		if (index < 0 || index >= array.length){
			throw IllegalIndexException.constructor();
		}
		return array[index];
	}

}
