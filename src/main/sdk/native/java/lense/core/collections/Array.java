package lense.core.collections;

import java.math.BigInteger;
import java.util.function.Function;

import lense.core.lang.Any;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;
import lense.core.lang.java.Property;
import lense.core.lang.java.Signature;
import lense.core.math.Natural;


@Signature("[=T<lense.core.lang.Any]::lense.core.collections.EditableSequence<T>")
public abstract class Array extends AbstractAssortment implements EditableSequence{

	@Constructor
	public static Array constructor (Natural size, Any seed){
		// TODO verify natural range
		return new NativeObjectArray(size, seed);
	}
	
	@Native
	public static  Array fromAnyArray (Any ... nativearray){
		// TODO verify natural range
		return new NativeObjectArray(nativearray);
	}
	
	@Native
	public static Array booleanArrayfromNativeNumberString (String value){
		BigInteger big = new BigInteger(value);
		
		boolean[] array = new boolean[big.bitLength()];
		for(int i = 0; i < big.bitLength();i++){
			array[i] = big.testBit(i);
		}

		return new NativeBooleanArray(array);
	}
	
	@Native
	public static <T> Array fromNative (T[] nativearray, Function<T, Any> transform){
		// TODO verify natural range
		return new NativeObjectArray(nativearray, transform);
	}
	
//	public static Array constructor (Natural size, Function<Natural , Any> init){
//		
//	}
	
	@Constructor(isImplicit = true)
	public static Array constructor (Sequence seq){
		// TODO verify natural range
		
		NativeObjectArray array = new NativeObjectArray(seq.getSize());
		Iterator iterator = seq.getIterator();
		int i=0;
		while(iterator.hasNext()){
			array.setPrimitive(i++, iterator.next());
		}
		return array;
	}

	@Override @Property(indexed = true , name = "")
	public abstract Any get(Natural index);
	
	@Override  @Property(indexed = true , name = "", setter = true)
	public abstract void  set(Natural index, Any value);
	
	@Override @Property(name = "size")
	public abstract Natural getSize();

	@Override @Property(name = "iterator")
	public abstract Iterator getIterator();
	
	@Override @Property(name = "indexes")
	public abstract Progression getIndexes();



}
