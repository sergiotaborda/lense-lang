package lense.core.collections;

import lense.core.lang.Any;
import lense.core.math.Natural;

public interface Sequence extends Iterable {

	public Any get(Natural index);
	public Natural getSize();
	public Progression getIndexes();
}
