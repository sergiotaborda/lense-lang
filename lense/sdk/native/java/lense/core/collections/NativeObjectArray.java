package lense.core.collections;

import java.util.Arrays;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.IllegalIndexException;
import lense.core.lang.Maybe;
import lense.core.lang.None;
import lense.core.lang.Some;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.reflection.JavaReifiedArguments;
import lense.core.math.Natural;

@PlatformSpecific
final class NativeObjectArray extends Array implements SmallArray{

	private Any[] array;
	
	public NativeObjectArray(Natural size){
		array = new Any[size.toPrimitiveInt()];
	}
	
	
	public NativeObjectArray(Any[] nativeArray ){
		array = nativeArray;
	}
	

	public void setAtPrimitiveIndex(int i, Any value){
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
	public Any getAtPrimitiveIndex(int index) {
		return array[index];
	}
	
	@Override
	public void set(Natural index, Any value) {
		if (index.toPrimitiveInt() >= array.length){
			throw IllegalIndexException.constructor(/*"Index from " + size + " on is not available"*/);
		}
		System.out.println("Seting " + value + " at index " + index);
		array[index.toPrimitiveInt()] = value;
	}

	@Override
	public Natural getSize() {
		return Natural.valueOfNative(array.length);
	}

	@Override
	public Iterator getIterator() {
		return new ArrayIterator(this, array.length);
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
	public boolean getEmpty() {
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
	
	@Override
	public Maybe indexOf(Any element) {
		for(int i =0; i < array.length; i++){
			if (array[i].equalsTo(element)){ 
				return Some.constructor( JavaReifiedArguments.getInstance().addType("lense.core.math.Natural") , Natural.valueOfNative(i));
			}
		}
		return None.NONE;
	}
	
	@Override
	public Array duplicate() {
		Any[] newArray = new Any[array.length];
		System.arraycopy(array, 0, newArray, 0, array.length);
		
		return new NativeObjectArray(newArray);
	}


	@Override
	public void copyTo(Array other) {
		if (other instanceof NativeObjectArray) {
			
			NativeObjectArray n = (NativeObjectArray)other;
			
			int length = Math.min(this.array.length, n.array.length);
			
			System.arraycopy(this.array, 0, n.array, 0,length);
			
		} else {
			throw new RuntimeException("Array to copy to is not an object array (found " + other.getClass().getName() + ")");
		}
		

	}


	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}



}
