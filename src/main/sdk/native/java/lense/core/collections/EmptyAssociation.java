package lense.core.collections;

import java.util.Collections;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.java.Native;
import lense.core.math.Int32;
import lense.core.math.Integer;
import lense.core.math.Natural;

@Native
public class EmptyAssociation implements Association {

	@Override
	public Boolean contains(Any other) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean containsAll(Assortment other) {
		return Boolean.FALSE;
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
	public Boolean getEmpty() {
		return Boolean.TRUE;
	}

	@Override
	public Boolean equalsTo(Any other) {
		return Boolean.valueOfNative(other instanceof EmptyAssociation);
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(0);
	}


	

}
