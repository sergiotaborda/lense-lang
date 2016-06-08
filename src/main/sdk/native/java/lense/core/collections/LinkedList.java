package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Property;
import lense.core.math.Natural;

public class LinkedList implements ResizableSequence {
	
	private java.util.LinkedList<Any> list;

	@Constructor(isImplicit = true)
	public static LinkedList constructor (Sequence seq){
		// TODO verify natural range
		
		LinkedList array = new LinkedList();
		Iterator iterator = seq.getIterator();
		while(iterator.hasNext().toPrimitiveBoolean()){
			array.add(iterator.next());
		}
		return array;
	}
	
	@Constructor
	public static LinkedList constructor (){
		return new LinkedList();
	}

	private LinkedList(){
		list = new java.util.LinkedList <>();
	}
	
	@Override @Property(indexed = true , name = "", setter = true)
	public void set(Natural index, Any element) {
		list.set(index.toPrimitiveInt(), element);
	}

	@Override @Property(indexed = true , name = "")
	public Any get(Natural index) {
		return list.get(index.toPrimitiveInt());
	}

	@Override
	public Natural getSize() {
		return Natural.valueOfNative(list.size());
	}

	@Override
	public Iterator getIterator() {
		return new IteratorAdapter(list.iterator());
	}

	@Override
	public void add(Any value) {
		 list.add(value);
	}

	@Override
	public void remove(Any value) {
		 list.remove(value);
	}

	@Override
	public Progression getIndexes() {
		return new NativeProgression(0, list.size() -1);
	}

}
