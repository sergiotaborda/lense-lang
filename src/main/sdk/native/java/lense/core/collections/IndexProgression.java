package lense.core.collections;

import lense.core.math.Natural;

public class IndexProgression implements Progression {

	private Natural first;
	private Natural last;

	public IndexProgression(Natural first, Natural last) {
		this.first = first;
		this.last = last;
	}

	@Override
	public Iterator getIterator() {
		// TODO Auto-generated method stub
		return null;
	}

}
