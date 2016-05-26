package lense.core.collections;

import java.util.Arrays;

import lense.core.lang.Any;
import lense.core.lang.IllegalIndexException;
import lense.core.lang.NativeInteraction;
import lense.core.math.Natural;

@NativeInteraction
public final class NativeIntRangeArray extends Array {

	private Any[] array;
	private Natural size;
	
	public NativeIntRangeArray(Natural size){
		array = new Any[size.toPrimitiveInt()];
	}
	
	public NativeIntRangeArray(Natural size, Any seed){
		array = new Any[size.toPrimitiveInt()];
		Arrays.fill(array,seed);
	}
	
	public void setPrimitive(int i, Any value){
		array[i] = value;
	}

	@Override
	public Any get(Natural index) {
		if (index.compareTo(size) >= 0){
			throw new IllegalIndexException(/*"Index from " + size + " on is not available"*/);
		}
		return array[index.toPrimitiveInt()];
	}
	
	@Override
	public void set(Natural index, Any value) {
		if (index.compareTo(size) >= 0){
			throw new IllegalIndexException(/*"Index from " + size + " on is not available"*/);
		}
		array[index.toPrimitiveInt()] = value;
	}

	@Override
	public Natural getSize() {
		return size;
	}

	@Override
	public Iterator getIterator() {
		// TODO Auto-generated method stub
		return null;
	}


}
