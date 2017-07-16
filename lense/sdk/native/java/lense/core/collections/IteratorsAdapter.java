package lense.core.collections;

import java.util.Iterator;

import lense.core.lang.Any;

class IteratorsAdapter implements Iterator<Any> {

	private lense.core.collections.Iterator iterable;

	public IteratorsAdapter(lense.core.collections.Iterator iterator) {
		this.iterable = iterator;
	}

	@Override
	public boolean hasNext() {
		return iterable.moveNext();
	}

	@Override
	public Any next() {
		return iterable.current();
	}

}
