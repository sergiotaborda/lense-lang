package lense.core.collections;

import java.math.BigInteger;
import java.util.function.Function;

import lense.core.lang.Any;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.MethodSignature;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.java.Property;
import lense.core.lang.java.Signature;
import lense.core.lang.reflection.ReifiedArguments;
import lense.core.math.Natural;


@Signature("[=T<lense.core.lang.Any]::lense.core.collections.EditableSequence<T>")
public abstract class Array extends AbstractAssortment implements EditableSequence{

	@Constructor
	public static Array constructor (ReifiedArguments args, Natural size, Any seed){
		
		return SizeArrayStrategy.resolveSizeStrategy(size).resolveStrategy(args).createArrayFrom(size, seed);
	}
	
	@PlatformSpecific
	public static  Array fromAnyArray (ReifiedArguments args,Any ... arrayOfAny){
		return SizeArrayStrategy.resolveStandardSizeStrategy().resolveStrategy(args).createArrayFrom(arrayOfAny);
	}
	
	@PlatformSpecific
	public static Array booleanArrayfromNativeNumberString (String value){
		BigInteger big = new BigInteger(value);
		
		boolean[] array = new boolean[big.bitLength()];
		for(int i = 0; i < big.bitLength();i++){
			array[i] = big.testBit(i);
		}

		return new NativeBooleanArray(array);
	}
	
	@PlatformSpecific
	public static <T> Array fromNative (ReifiedArguments args,T[] nativeArray, Function<T, Any> transform){

		Any[] array = new Any[nativeArray.length];

		for(int i =0; i< array.length; i++){
			array[i] = transform.apply(nativeArray[i]);
		}
		
		return SizeArrayStrategy.resolveStandardSizeStrategy().resolveStrategy(args).createArrayFrom(array);
	}
	
	@Constructor(isImplicit = true)
	@MethodSignature( returnSignature = "lense.core.collections.Array<T>", paramsSignature = "lense.core.collections.Sequence<T>")
	public static Array constructor (ReifiedArguments args, Sequence seq){
		return SizeArrayStrategy.resolveSizeStrategy(seq.getSize()).resolveStrategy(args).createArrayFrom(seq);
	}

	@Override @Property(indexed = true ) 
	@MethodSignature( returnSignature = "T" , paramsSignature = "lense.core.math.Natural")
	public abstract Any get(Natural index);
	
	@Override  @Property(indexed = true , setter = true)
	@MethodSignature( returnSignature = "" , paramsSignature = "lense.core.math.Natural,T")
	public abstract void  set(Natural index, Any value);
	
	@Override @Property(name = "size")
	public abstract Natural getSize();

	@Override @Property(name = "iterator")
	@MethodSignature( returnSignature = "lense.core.collections.Iterator<T>", paramsSignature = "")
	public abstract Iterator getIterator();
	
	@Override @Property(name = "indexes")
	@MethodSignature( returnSignature = "lense.core.collections.Progression<lense.core.math.Natural>", paramsSignature = "")
	public abstract Progression getIndexes();

	
	public abstract boolean contains(Any other);
	
	public boolean containsAll(Assortment other) {
		if (this.isEmpty()) {
			return other.isEmpty();
		}
		
		Iterator it = other.getIterator();
		while (it.moveNext()) {
			if (!this.contains(it.current())) {
				return false;
			}
		}
		return true;
	}

}
