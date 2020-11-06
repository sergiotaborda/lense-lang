package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.java.Signature;


@Signature("[+T<lense.core.lang.Any]:lense.core.collections.Iterable<T>:")
public interface Assortment extends Any,Iterable, Countable{

	public boolean contains(Any other);
	public boolean containsAll(Assortment other);
	
	public lense.core.lang.String asString();
}
