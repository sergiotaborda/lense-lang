package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Property;
import lense.core.lang.java.Signature;
import lense.core.math.Integer;
import lense.core.math.Natural;

@Signature("[+T<lense.core.lang.Any]::lense.core.collections.ResizableSequence<T>")
public class LinkedList extends AbstractAssortment implements ResizableSequence {
	
	private java.util.LinkedList<Any> list;

	@Constructor(isImplicit = true)
	public static LinkedList constructor (Sequence seq){
		// TODO verify natural range
		
		LinkedList array = new LinkedList();
		Iterator iterator = seq.getIterator();
		while(iterator.moveNext()){
			array.add(iterator.current());
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

	@Override
	public boolean contains(Any other) {
		return list.contains(other);
	}

	@Override
	public boolean containsAll(Assortment other) {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
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
