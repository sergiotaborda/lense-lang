package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Property;
import lense.core.math.Natural;

public class List implements ResizableSequence {

	private java.util.ArrayList<Any> list;

	public List(Natural capacity) {
		list = new java.util.ArrayList<>(capacity.toPrimitiveInt());
	}

	public List() {
		list = new java.util.ArrayList<>();
	}

	@Constructor
	public static List constructor() {
		return new List();
	}

	@Constructor(isImplicit = true)
	public static List constructor(Sequence seq) {
		// TODO verify natural range

		List array = new List(seq.getSize());
		Iterator iterator = seq.getIterator();
		while (iterator.hasNext().toPrimitiveBoolean()) {
			array.add(iterator.next());
		}
		return array;
	}

	@Property(indexed = true, setter = true)
	public void set(Natural index, Any value) {
		list.set(index.toPrimitiveInt(), value);
	}

	@Override
	@Property(indexed = true)
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
