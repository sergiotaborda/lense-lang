package lense.core.lang.java;

import java.util.ArrayList;
import java.util.List;

import lense.core.collections.Iterator;
import lense.core.lang.Any;

public final class ComposedIterator implements Iterator {

	public static ComposedIterator iterate(Iterator other) {
		return new ComposedIterator(other);
	}
	
	private final List<Iterator> iterators = new ArrayList<>(2);
	
	private int pos = -1;
	private Iterator iterator;
	
	private ComposedIterator(Iterator other) {
		iterators.add(other);
	}
	
	public ComposedIterator then(Iterator other) {
		iterators.add(other);
		return this;
	}
	
	@Override
	public boolean moveNext() {
		if (pos < 0) {
			pos++;
		    iterator = iterators.get(pos);
		}
		if (iterator.moveNext()) {
			return true;
		} else if (++pos < iterators.size()) {
		    iterator = iterators.get(pos);
			return iterator.moveNext();
		}
		return false;
	}

	@Override
	public Any current() {
		return iterator.current();
	}

}
