package lense.core.collections;

import java.util.Arrays;
import java.util.function.Function;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
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
	
	public NativeObjectArray(Any[] nativeArray ){
		array = nativeArray;
	}
	
	public <T> NativeObjectArray(T[] nativeArray , Function<T, Any> transform){
		array = new Any[nativeArray.length];

		for(int i =0; i< array.length; i++){
			array[i] = transform.apply(nativeArray[i]);
		}
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

	@Override
	public boolean contains(Any other) {
		for(Any a : array){
			if (a.equalsTo(other)){ // TODO use lense equals function
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isEmpty() {
		return array.length == 0;
	}

	@Override
	public boolean equalsTo(Any other) {
		return other instanceof NativeObjectArray && Arrays.equals(((NativeObjectArray)other).array , this.array);
	}

	@Override
	public HashValue hashValue() {
		return new HashValue(array.length);
	}
}
