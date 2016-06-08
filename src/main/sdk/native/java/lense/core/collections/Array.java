package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Property;
import lense.core.math.Natural;

public abstract class Array implements EditableSequence{

	@Constructor
	public static Array constructor (Natural size, Any seed){
		// TODO verify natural range
		return new NativeObjectArray(size, seed);
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
		while(iterator.hasNext().toPrimitiveBoolean()){
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
