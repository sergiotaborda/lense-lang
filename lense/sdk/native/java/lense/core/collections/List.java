package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Property;
import lense.core.lang.java.Signature;
import lense.core.math.Integer;
import lense.core.math.Natural;

@Signature("[+T<lense.core.lang.Any]::lense.core.collections.ResizableSequence<T>")
public class List extends AbstractAssortment implements ResizableSequence {

	private java.util.ArrayList<Any> list;

	public List(Natural capacity) {
		list = new java.util.ArrayList<>(capacity.toPrimitiveInt());
	}

	public List() {
		list = new java.util.ArrayList<>();
	}

    @Constructor(paramsSignature = "")
	public static List constructor() {
		return new List();
	}

	@Constructor(isImplicit = true, paramsSignature = "")
	public static List constructor(Sequence seq) {
		// TODO verify natural range

		List array = new List(seq.getSize());
		Iterator iterator = seq.getIterator();
		while (iterator.moveNext()) {
			array.add(iterator.current());
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

	@Override
	public boolean getEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean contains(Any other) {
		return list.contains(other);
	}

	@Override
	public boolean containsAll(Assortment other) {
	   Iterator it = other.getIterator();
	   while (it.moveNext()){
	       if (contains(it.current())){
	           return true;
	       }
	   }
	   return false;
	}

	@Override
	public boolean equalsTo(Any other) {
		throw new UnsupportedOperationException();
	}

	@Override
	public HashValue hashValue() {
		throw new UnsupportedOperationException();
	}

}
