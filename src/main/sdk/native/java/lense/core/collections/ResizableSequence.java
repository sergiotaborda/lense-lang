package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.java.Signature;

@Signature("[+T<lense.core.lang.Any]::lense.core.collections.EditableSequence<T>")
public interface ResizableSequence extends EditableSequence {

	public void add(Any value);
	public void remove(Any value);
}
