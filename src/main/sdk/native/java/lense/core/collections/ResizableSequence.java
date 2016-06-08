package lense.core.collections;

import lense.core.lang.Any;

public interface ResizableSequence extends EditableSequence {

	public void add(Any value);
	public void remove(Any value);
}
