package lense.core.collections;

import java.util.Arrays;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.HashValue;
import lense.core.lang.IllegalIndexException;
import lense.core.lang.java.Native;
import lense.core.math.Int32;
import lense.core.math.Integer;
import lense.core.math.Natural;

@Native
final class NativeBooleanArray extends Array {

	private boolean[] array;
	
	public NativeBooleanArray(Natural size){
		array = new boolean[size.toPrimitiveInt()];
	}
	
	public NativeBooleanArray(Natural size, boolean seed){
		array = new boolean[size.toPrimitiveInt()];
		Arrays.fill(array,seed);
	}
	
	public NativeBooleanArray(boolean[] nativeArray ){
		array = nativeArray;
	}
	
	public void setPrimitive(int i, Any value){
		
		array[i] = ((Boolean)value).toPrimitiveBoolean();
	}

	@Override
	public Any get(Natural index) {
		if (index.toPrimitiveInt() >= array.length){
			throw IllegalIndexException.constructor(/*"Index from " + size + " on is not available"*/);
		}
		return Boolean.valueOfNative(array[index.toPrimitiveInt()]);
	}
	
	@Override
	public void set(Natural index, Any value) {
		if (index.toPrimitiveInt() >= array.length){
			throw IllegalIndexException.constructor(/*"Index from " + size + " on is not available"*/);
		}
		array[index.toPrimitiveInt()] = ((Boolean)value).toPrimitiveBoolean();
	}

	@Override
	public Natural getSize() {
		return Natural.valueOfNative(array.length);
	}

	@Override
	public Iterator getIterator() {
		return new BooleanArrayIterator(array);
	}

	@Override
	public Progression getIndexes() {
		return new NativeProgression(0, array.length -1);
	}

	@Override
	public boolean contains(Any other) {
		boolean val = ((Boolean)other).toPrimitiveBoolean();
		for(boolean a : array){
			if (a == val){ 
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(Assortment other) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty() {
		return array.length == 0;
	}

	@Override
	public boolean equalsTo(Any other) {
		return other instanceof NativeBooleanArray && Arrays.equals(((NativeBooleanArray)other).array,this.array);
	}

	@Override
	public HashValue hashValue() {
		return new HashValue(array.length);
	}
}
