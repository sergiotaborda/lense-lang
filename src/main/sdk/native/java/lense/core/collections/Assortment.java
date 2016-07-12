package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.java.Signature;


@Signature("[+T<lense.core.lang.Any]::lense.core.collections.Iterable<T>")
public interface Assortment extends Iterable, Countable{

	public Boolean contains(Any other);
	public Boolean containsAll(Assortment other);
}
