package lense.core.collections;

public interface Sequence extends lense.core.collections.Iterable , lense.core.lang.Any{
	
public lense.core.lang.Any get(lense.core.math.Natural  index);	
public lense.core.math.Natural getSize();
}
