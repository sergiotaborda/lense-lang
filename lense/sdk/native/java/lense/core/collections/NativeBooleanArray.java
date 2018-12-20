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
		if (index.toPrimitiveInt() >= array.length){
			throw IllegalIndexException.constructor(/*"Index from " + size + " on is not available"*/);
		}
		return getAtPrimitiveIndex(index.toPrimitiveInt());
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
				return Some.constructor( JavaReifiedArguments.getInstance().addType(lense.core.math.Natural.TYPE_RESOLVER), Natural64.valueOfNative(i));
			}
		}
		return None.NONE;
	}


	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public Array copyTo(Array other, Natural sourceIndex, Natural destinationIndex, Natural length) {
		
		if (other instanceof NativeBooleanArray) {
			
			NativeBooleanArray n = (NativeBooleanArray)other;
			
			if (sourceIndex.toPrimitiveInt() < 0 || sourceIndex.toPrimitiveInt() >=  n.array.length  ) {
				throw new IllegalArgumentException("sourceIndex out of bounds " + sourceIndex.toPrimitiveInt());
			}
			
			if (destinationIndex.toPrimitiveInt() < 0 || destinationIndex.toPrimitiveInt() >=  n.array.length  ) {
				throw new IllegalArgumentException("destinationIndex out of bounds " + destinationIndex.toPrimitiveInt());
			}
			
			if (length.toPrimitiveInt() < 0 || destinationIndex.toPrimitiveInt() + length.toPrimitiveInt() >  n.array.length  ) {
				throw new IllegalArgumentException("length out of bounds " + length.toPrimitiveInt());
			}
			
			System.arraycopy(this.array, sourceIndex.toPrimitiveInt(), n.array, destinationIndex.toPrimitiveInt(),length.toPrimitiveInt());
			
			return other;
		} else {
			throw new RuntimeException("Array to copy to is not a boolean array");
		}
		
	}


	public Type type() {
		return TYPE;
	}
}
