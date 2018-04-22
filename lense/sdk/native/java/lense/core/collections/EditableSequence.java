package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.java.Property;
import lense.core.lang.java.Signature;
import lense.core.math.Natural;

@Signature("[+T<lense.core.lang.Any]::lense.core.collections.Sequence<T>")
public interface EditableSequence extends Sequence {

	@Property(indexed = true)
	public void set(Natural index, Any value);
}
