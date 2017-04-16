package lense.core.collections;

public interface ResizableSequence extends lense.core.collections.EditableSequence , lense.core.lang.Any{
	
public void add(lense.core.lang.Any  element);	
public lense.core.lang.Any remove(lense.core.lang.Any  element);
}
