package lense.core.collections;

import lense.core.lang.Any;

public interface ResizableSequence extends EditableSequence {

	public void add(Any value);
	public Any remove(Any value);
}
