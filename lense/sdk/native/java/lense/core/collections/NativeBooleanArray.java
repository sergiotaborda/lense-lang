package lense.core.collections;

import java.util.Arrays;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.HashValue;
import lense.core.lang.IllegalIndexException;
import lense.core.lang.Maybe;
import lense.core.lang.None;
import lense.core.lang.Some;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.reflection.JavaReifiedArguments;
import lense.core.lang.reflection.Type;
import lense.core.math.NativeNumberFactory;
import lense.core.math.Natural;
import lense.core.math.Natural64;

@PlatformSpecific
final class NativeBooleanArray extends Array implements SmallArray {

	private final static Type TYPE = Array.RAW_TYPE.withGenerics(Boolean.TYPE);
	private boolean[] array;
	
	public NativeBooleanArray(int size){
		array = new boolean[size];
	}

	
	public NativeBooleanArray(boolean[] nativeArray ){
		array = nativeArray;
	}
	
	public void setAtPrimitiveIndex(int i, Any value){
		
		array[i] = ((Boolean)value).toPrimitiveBoolean();
	}

	@Override
	public Any getAtPrimitiveIndex(int index) {
		return Boolean.valueOfNative(array[index]);
	}
	
	@Override
	public Any get(Natural index) {
		if (NativeNumberFactory.naturalToPrimitiveInt(index) >= array.length){
			throw IllegalIndexException.constructor(/*"Index from " + size + " on is not available"*/);
		}
		return getAtPrimitiveIndex(NativeNumberFactory.naturalToPrimitiveInt(index));
	}
	
	@Override
	public void set(Natural index, Any value) {
		if (NativeNumberFactory.naturalToPrimitiveInt(index) >= array.length){
			throw IllegalIndexException.constructor(/*"Index from " + size + " on is not available"*/);
		}
		array[NativeNumberFactory.naturalToPrimitiveInt(index)] = ((Boolean)value).toPrimitiveBoolean();
	}

	@Override
	public Natural getSize() {
		return Natural64.valueOfNative(array.length);
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
	public boolean getEmpty() {
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


	
	@Override
	public Array duplicate() {
		boolean[] newArray = new boolean[array.length];
		System.arraycopy(array, 0, newArray, 0, array.length);
		
		return new NativeBooleanArray(newArray);
	}


	@Override
	public Array copyTo(Array other) {
		if (other instanceof NativeBooleanArray) {
			
			NativeBooleanArray n = (NativeBooleanArray)other;
			
			int length = Math.min(this.array.length, n.array.length);
			
			System.arraycopy(this.array, 0, n.array, 0,length);
			
			return other;
		} else {
			throw new RuntimeException("Array to copy to is not a boolean array");
		}
	}


	
	@Override
	public Maybe indexOf(Any element) {
		boolean val = ((Boolean)element).toPrimitiveBoolean();
		for(int i =0; i < array.length; i++){
			if (array[i] == val){ 
				return Some.constructor( JavaReifiedArguments.getInstance().addType(NativeNumberFactory.NATURAL_TYPE_RESOLVER), Natural64.valueOfNative(i));
			}
		}
		return None.NONE;
	}


	@Override
	public int size() {
		return this.size();
	}


	@Override
	public Array copyTo(Array other, Natural sourceIndex, Natural destinationIndex, Natural length) {
		
		if (other instanceof NativeBooleanArray) {
			
			NativeBooleanArray n = (NativeBooleanArray)other;
			
			if (NativeNumberFactory.naturalToPrimitiveInt(sourceIndex) < 0 || NativeNumberFactory.naturalToPrimitiveInt(sourceIndex) >=  n.array.length  ) {
				throw new IllegalArgumentException("sourceIndex out of bounds " + sourceIndex);
			}
			
			if (NativeNumberFactory.naturalToPrimitiveInt(destinationIndex) < 0 || NativeNumberFactory.naturalToPrimitiveInt(destinationIndex) >=  n.array.length  ) {
				throw new IllegalArgumentException("destinationIndex out of bounds " + destinationIndex);
			}
			
			if (NativeNumberFactory.naturalToPrimitiveInt(length) < 0 || NativeNumberFactory.naturalToPrimitiveInt(destinationIndex) +  NativeNumberFactory.naturalToPrimitiveInt(length) >  n.array.length  ) {
				throw new IllegalArgumentException("length out of bounds " + length);
			}
			
			System.arraycopy(this.array, NativeNumberFactory.naturalToPrimitiveInt(sourceIndex), n.array, NativeNumberFactory.naturalToPrimitiveInt(destinationIndex) , NativeNumberFactory.naturalToPrimitiveInt(length));
			
			return other;
		} else {
			throw new RuntimeException("Array to copy to is not a boolean array");
		}
		
	}


	public Type type() {
		return TYPE;
	}
}
