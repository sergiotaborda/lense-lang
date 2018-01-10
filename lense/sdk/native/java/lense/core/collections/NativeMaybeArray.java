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
final class NativeMaybeArray extends Array implements SmallArray {

	private Any[] array; // array of the object inside the maybe
	private String innerTypeName;
	
	public NativeMaybeArray(String innerTypeName, int size){
		this.array = new Any[size];
		this.innerTypeName = innerTypeName;
	}

	private NativeMaybeArray(String innerTypeName, Any[] stripedArray){
		this.array = stripedArray;
		this.innerTypeName = innerTypeName;
	}
	
	@Override
	public int size() {
		return array.length;
	}


	@Override
	public Any getAtPrimitiveIndex(int index) {
		Any value = array[index];
		
		if (value == null) {
			return None.NONE;
		}
		
		return Some.constructor( JavaReifiedArguments.getInstance().addType(innerTypeName) , value);

	}
	
	
	@Override
	public void setAtPrimitiveIndex(int i, Any value) {
		Maybe maybe = ((Maybe)value);
		
		if (maybe.isPresent()) {
			array[i] = maybe.getValue();
		} else {
			array[i] = null;
		}
	}


	@Override
	public Any get(Natural index) {
		int pIndex = index.toPrimitiveInt();
		if (pIndex >= array.length){
			throw IllegalIndexException.constructor(/*"Index from " + size + " on is not available"*/);
		}
		
		return getAtPrimitiveIndex(pIndex);

	}
	
	@Override
	public void set(Natural index, Any value) {
		if (index.toPrimitiveInt() >= array.length){
			throw IllegalIndexException.constructor(/*"Index from " + size + " on is not available"*/);
		}
		
		setAtPrimitiveIndex(index.toPrimitiveInt(), value);
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
		if (!(other instanceof Maybe)) {
			return false;
		}
		
		Maybe maybe = (Maybe)other;

		for(Any a : array){
			if ((a == null && maybe.isAbsent()) || (a != null && maybe.is(a))){ 
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
	public boolean getEmpty() {
		return array.length == 0;
	}

	@Override
	public boolean equalsTo(Any other) {
		return other instanceof NativeMaybeArray && Arrays.equals(((NativeMaybeArray)other).array,this.array);
	}

	@Override
	public HashValue hashValue() {
		return new HashValue(array.length);
	}


	
	@Override
	public Array duplicate() {
		Any[] newArray = new Any[array.length];
		System.arraycopy(array, 0, newArray, 0, array.length);
		
		return new NativeMaybeArray( innerTypeName,newArray);
	}


	@Override
	public void copyTo(Array other) {
		if (other instanceof NativeMaybeArray) {
			
			NativeMaybeArray n = (NativeMaybeArray)other;
			
			int length = Math.min(this.array.length, n.array.length);
			
			System.arraycopy(this.array, 0, n.array, 0,length);
			
		} else {
			throw new RuntimeException("Array to copy to is not a maybe array (" +  other.getClass().getName() +")");
		}
	}


	
	@Override
	public Maybe indexOf(Any element) {
		Maybe maybe = (Maybe)element;

		for(int i =0; i < array.length; i++){
			Any a = array[i];
			if ((a == null && maybe.isAbsent()) || (a != null && maybe.is(a))){ 
				return Some.constructor( JavaReifiedArguments.getInstance().addType("lense.core.math.Natural") , Natural.valueOfNative(i));
			} 
		}
		return None.NONE;
	}





}
