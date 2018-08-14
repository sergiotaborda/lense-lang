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
import lense.core.lang.reflection.Type;
import lense.core.math.Natural;

@PlatformSpecific
final class NativeObjectArray extends Array implements SmallArray{

	private final Any[] array;
	private final Type type;
	
	public NativeObjectArray(Natural size, Type innerType){
		array = new Any[size.toPrimitiveInt()];
		this.type = Array.RAW_TYPE.withGenerics(innerType);
	}
	
	
	public NativeObjectArray(Any[] nativeArray, Type innerType){
		this.array = nativeArray;
		this.type = Array.RAW_TYPE.withGenerics(innerType);
	}
	
	private NativeObjectArray(NativeObjectArray other){
		
		this.type = other.type;
		this.array = new Any[other.array.length];
		System.arraycopy(other.array, 0, this.array, 0, other.array.length);

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
				return Some.constructor( JavaReifiedArguments.getInstance().addType(lense.core.math.Natural.TYPE_RESOLVER) , Natural.valueOfNative(i));
			}
		}
		return None.NONE;
	}
	
	@Override
	public Array duplicate() {
		return new NativeObjectArray(this);
	}


	@Override
	public Array copyTo(Array other) {
		if (other instanceof NativeObjectArray) {
			
			NativeObjectArray n = (NativeObjectArray)other;
			
			int length = Math.min(this.array.length, n.array.length);
			
			System.arraycopy(this.array, 0, n.array, 0,length);
			
			return other;
		} else {
			throw new RuntimeException("Array to copy to is not an object array (found " + other.getClass().getName() + ")");
		}
		

	}


	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public Array copyTo(Array other, Natural sourceIndex, Natural destinationIndex, Natural length) {
		if (other instanceof NativeBooleanArray) {
			
			NativeObjectArray n = (NativeObjectArray)other;
			
			System.arraycopy(this.array, sourceIndex.toPrimitiveInt(), n.array, destinationIndex.toPrimitiveInt(),length.toPrimitiveInt());
			
			return other;
		} else {
			throw new RuntimeException("Array to copy to is not a boolean array");
		}
		
	}


	
	@Override
	public Type type() {
		return type;
	}



}
