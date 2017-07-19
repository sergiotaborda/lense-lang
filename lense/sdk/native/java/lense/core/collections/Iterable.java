package lense.core.collections;

import lense.core.lang.java.Signature;


@Signature("[+T<lense.core.lang.Any]::")
public interface Iterable extends lense.core.lang.Any {

	public Iterator getIterator();

}

