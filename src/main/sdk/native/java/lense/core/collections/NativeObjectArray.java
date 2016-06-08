package lense.core.collections;

import java.util.Arrays;

import lense.core.lang.Any;
import lense.core.lang.IllegalIndexException;
import lense.core.lang.java.Native;
import lense.core.math.Natural;

@Native
final class NativeObjectArray extends Array {

	private Any[] array;
	
	public NativeObjectArray(Natural size){
		array = new Any[size.toPrimitiveInt()];
	}
	
	public NativeObjectArray(Natural size, Any seed){
		array = new Any[size.toPrimitiveInt()];
		Arrays.fill(array,seed);
	}
	
	public void setPrimitive(int i, Any value){
		array[i] = value;
	}

	@Override
	public Any get(Natural index) {
		if (index.toPrimitiveInt() >= array.length){
			throw IllegalIndexException.constructor(/*"Index from " + size + " on is not available"*/);
		}
		return array[index.toPrimitiveInt()];
	}
	
	@Override
	public void set(Natural index, Any value) {
		if (index.toPrimitiveInt() >= array.length){
			throw IllegalIndexException.constructor(/*"Index from " + size + " on is not available"*/);
		}
		array[index.toPrimitiveInt()] = value;
	}

	@Override
	public Natural getSize() {
		return Natural.valueOfNative(array.length);
	}

	@Override
	public Iterator getIterator() {
		return new ArrayIterator(array);
	}

	@Override
	public Progression getIndexes() {
		return new NativeProgression(0, array.length -1);
	}


}
