package lense.core.collections;

import java.util.Collections;

import lense.core.lang.Any;
import lense.core.lang.Maybe;
import lense.core.lang.None;
import lense.core.lang.String;
import lense.core.lang.java.Native;
import lense.core.math.Int32;
import lense.core.math.Integer;
import lense.core.math.Natural;

@Native
public class EmptyAssociation implements Association {

	@Override
	public boolean contains(Any other) {
		return false;
	}

	@Override
	public boolean containsAll(Assortment other) {
		return other.isEmpty();
	}

	@Override
	public Iterator getIterator() {
		return new IteratorAdapter(Collections.emptyIterator());
	}

	@Override
	public Natural getSize() {
		return Natural.valueOfNative(0);
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean equalsTo(Any other) {
		return other instanceof EmptyAssociation;
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(0);
	}

	@Override
	public String asString() {
		return String.valueOfNative("{}");
	}

	@Override
	public Maybe get(Any key) {
		return None.NONE;
	}


	

}