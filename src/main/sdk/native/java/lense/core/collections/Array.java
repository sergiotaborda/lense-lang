package lense.core.collections;

import lense.core.lang.Any;
import lense.core.math.Natural;

public abstract class Array implements EditableSequence{

	public static Array constructor (Natural size, Any seed){
		// TODO verify natural range
		return new NativeIntRangeArray(size, seed);
	}
	
//	public static Array constructor (Natural size, Function<Natural , Any> init){
//		
//	}
	
	public static Array constructor (Sequence seq){
		// TODO verify natural range
		
		NativeIntRangeArray array = new NativeIntRangeArray(seq.getSize());
		Iterator iterator = seq.getIterator();
		int i=0;
		while(iterator.hasNext().toPrimitiveBoolean()){
			array.setPrimitive(i++, iterator.next());
		}
		return array;
	}

	@Override
	public abstract Any get(Natural index);
	
	@Override
	public abstract void  set(Natural index, Any value);
	
	@Override
	public abstract Natural getSize();

	@Override
	public abstract Iterator getIterator();
	




}
