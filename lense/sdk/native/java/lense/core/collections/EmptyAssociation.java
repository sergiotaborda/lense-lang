package lense.core.collections;

import java.util.Collections;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.Maybe;
import lense.core.lang.None;
import lense.core.lang.String;
import lense.core.lang.java.Base;
import lense.core.lang.java.Native;
import lense.core.lang.java.PlatformSpecific;
import lense.core.math.Natural;

@PlatformSpecific
public class EmptyAssociation extends Base implements Association {

	@Override
	public boolean contains(Any other) {
		return false;
	}

	@Override
	public boolean containsAll(Assortment other) {
		return other.getEmpty();
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
	public boolean getEmpty() {
		return true;
	}

	@Override
	public boolean equalsTo(Any other) {
		return other instanceof EmptyAssociation;
	}

	@Override
	public HashValue hashValue() {
		return new HashValue(0);
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
