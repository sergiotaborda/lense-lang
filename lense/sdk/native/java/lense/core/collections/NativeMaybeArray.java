package lense.core.collections;

import java.util.Arrays;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.IllegalIndexException;
import lense.core.lang.Maybe;
import lense.core.lang.None;
import lense.core.lang.Some;
import lense.core.lang.java.JavaReifiedArguments;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.reflection.Type;
import lense.core.lang.reflection.TypeResolver;
import lense.core.math.NativeNumberFactory;
import lense.core.math.Natural;
import lense.core.math.Natural64;

@PlatformSpecific
final class NativeMaybeArray extends Array implements SmallArray {

	private final Any[] array; // array of the object inside the maybe
	private final Type type;
	
	public NativeMaybeArray(Type innerType, int size){
		this.array = new Any[size];
		this.type = Array.RAW_TYPE.withGenerics(innerType);
	}

	private NativeMaybeArray(NativeMaybeArray other){
		this.type = other.type;
		this.array = new Any[other.array.length];
		System.arraycopy(other.array, 0, this.array, 0, other.array.length);
	}
	
	public Type type() {
		return 	type;
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
		
		return Some.constructor( JavaReifiedArguments.getInstance().addType(TypeResolver.of(type.getGenericTypeAt(0))) , value);

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
		int pIndex =  NativeNumberFactory.naturalToPrimitiveInt(index);
		if (pIndex >= array.length){
			throw IllegalIndexException.constructor(/*"Index from " + size + " on is not available"*/);
		}
		
		return getAtPrimitiveIndex(pIndex);

	}
	
	@Override
	public void set(Natural index, Any value) {
		if (NativeNumberFactory.naturalToPrimitiveInt(index) >= array.length){
			throw IllegalIndexException.constructor(/*"Index from " + size + " on is not available"*/);
		}
		
		setAtPrimitiveIndex(NativeNumberFactory.naturalToPrimitiveInt(index), value);
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
		if (!(other instanceof Maybe)) {
			return false;
		}
		
		Maybe maybe = (Maybe)other;

		for(Any a : array){
			if ((a == null && maybe.isAbsent()) || (a != null && maybe.valueEqualsTo(a))){ 
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
		return new NativeMaybeArray(this);
	}


	@Override
	public Array copyTo(Array other) {
		if (other instanceof NativeMaybeArray) {
			
			NativeMaybeArray n = (NativeMaybeArray)other;
			
			int length = Math.min(this.array.length, n.array.length);
			
			System.arraycopy(this.array, 0, n.array, 0,length);
			
			return other;
		} else {
			throw new RuntimeException("Array to copy to is not a maybe array (" +  other.getClass().getName() +")");
		}
	}


	
	@Override
	public Maybe indexOf(Any element) {
		Maybe maybe = (Maybe)element;

		for(int i =0; i < array.length; i++){
			Any a = array[i];
			if ((a == null && maybe.isAbsent()) || (a != null && maybe.valueEqualsTo(a))){ 
				return Some.constructor( JavaReifiedArguments.getInstance().addType(NativeNumberFactory.NATURAL_TYPE_RESOLVER) , Natural64.valueOfNative(i));
			} 
		}
		return None.NONE;
	}

	@Override
	public Array copyTo(Array other, Natural sourceIndex, Natural destinationIndex, Natural length) {
		
		if (other instanceof NativeBooleanArray) {
			
			NativeMaybeArray n = (NativeMaybeArray)other;
			
			System.arraycopy(this.array, NativeNumberFactory.naturalToPrimitiveInt(sourceIndex), n.array,   NativeNumberFactory.naturalToPrimitiveInt(destinationIndex), NativeNumberFactory.naturalToPrimitiveInt(length));
			
			return other;
		} else {
			throw new RuntimeException("Array to copy to is not a boolean array");
		}
		
	}





}
