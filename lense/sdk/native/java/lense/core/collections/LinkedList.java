package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.MethodSignature;
import lense.core.lang.java.Property;
import lense.core.lang.java.Signature;
import lense.core.lang.reflection.ReifiedArguments;
import lense.core.math.NativeNumberFactory;
import lense.core.math.Natural;
import lense.core.math.Natural64;

@Signature("[+T<lense.core.lang.Any]::lense.core.collections.ResizableSequence<T>")
public class LinkedList extends AbstractAssortment implements ResizableSequence {
	
	private java.util.LinkedList<Any> list;

	@Constructor(isImplicit = true, paramsSignature = "lense.core.collections.Sequence<T>")
	public static LinkedList constructor (ReifiedArguments args, Sequence seq){
		// TODO verify natural range
		
		LinkedList array = new LinkedList();
		Iterator iterator = seq.getIterator();
		while(iterator.moveNext()){
			array.add(iterator.current());
		}
		return array;
	}
	
    @Constructor(paramsSignature = "")
	public static LinkedList constructor (ReifiedArguments args){
		return new LinkedList();
	}

	private LinkedList(){
		list = new java.util.LinkedList <>();
	}
	
	@Override @Property(indexed = true , name = "", setter = true)
	@MethodSignature( returnSignature = "" , paramsSignature = "lense.core.math.Natural,T", declaringType = "lense.core.collections.EditableSequence" , override = true)
	public void set(Natural index, Any element) {
		list.set(NativeNumberFactory.naturalToPrimitiveInt(index), element);
	}

	@Override @Property(indexed = true , name = "")
    @MethodSignature( returnSignature = "T" , paramsSignature = "lense.core.math.Natural",declaringType = "lense.core.collections.Sequence" , override = true)
	public Any get(Natural index) {
		return list.get(NativeNumberFactory.naturalToPrimitiveInt(index));
	}

	@Override @Property(name = "size")
	public Natural getSize() {
		return Natural64.valueOfNative(list.size());
	}

	@Override @Property(name = "iterator")
    @MethodSignature( returnSignature = "lense.core.collections.Iterator<T>", paramsSignature = "",declaringType = "lense.core.collections.Iterable" , override = true)
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

    @Override @Property(name = "indexes")
    @MethodSignature( returnSignature = "lense.core.collections.Progression<lense.core.math.Natural>", paramsSignature = "",declaringType = "lense.core.collections.Sequence" , override = true)
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
	public boolean getEmpty() {
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
