package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.Function;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.java.Signature;

@Signature("[+T<lense.core.lang.Any]::")
public interface Iterator {

	public boolean moveNext();
	public lense.core.lang.Any current();
	
	@PlatformSpecific
	public default Iterator map(Function transformer) {
		return new MapIterator(this, transformer);
	}
}


record MapIterator(Iterator original,Function transformer) implements Iterator {
	

	@Override
	public boolean moveNext() {
		return original.moveNext();
	}

	@Override
	public Any current() {
		return transformer.apply(original.current());
	}

	
}