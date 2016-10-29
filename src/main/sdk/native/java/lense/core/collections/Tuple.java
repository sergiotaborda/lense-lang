package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.IllegalIndexException;
import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Property;
import lense.core.math.Int32;
import lense.core.math.Integer;
import lense.core.math.Natural;

public class Tuple extends Base implements Any, Iterable {

	public static Tuple valueOf(Any value, Tuple next){
		return new Tuple(value, next);
	}
	
	@Constructor(isImplicit = true)
	public static Tuple valueOf(Any value){
		return new Tuple(value, null);
	}


	private Tuple tail;
	private Any head;
	
	public Tuple(Any head, Tuple tail){
		this.head = head;
		this.tail = tail;
	}
	
	@Property(indexed = true)
	public Any get(Natural index){
		if (index.isZero()){
			return head;
		} else if (tail != null) {
			return tail.get(index.predecessor());
		} else {
			throw IllegalIndexException.constructor();
		}
	}
	
	public Any head(){
		return head;
	}
	
	public Tuple tail(){
		if (tail == null){
			throw IllegalIndexException.constructor();
		}
		return tail;
	}
	
	
	private boolean equals(Tuple other){
		if (this.tail == null){
			return this.head.equals(other.head) && (other.tail == null);
		} else {
			return this.head.equals(other.head) && this.tail.equals(other.tail);
		}
	}
	
	@Override
	public boolean equalsTo(Any other) {
		return other instanceof Tuple && equals((Tuple)other);
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(this.head.hashCode());
	}

	@Override
	public Iterator getIterator() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
}
