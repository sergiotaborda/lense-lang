package lense.core.collections;

import lense.core.lang.Any;
import lense.core.math.Natural;

public interface EditableSequence extends Sequence {

	public void set(Natural index, Any value);
}
