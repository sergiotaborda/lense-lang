package lense.core.collections;

import lense.core.lang.java.MethodSignature;
import lense.core.lang.java.Property;
import lense.core.lang.java.Signature;


@Signature("[+T<lense.core.lang.Any]::")
public interface Iterable extends lense.core.lang.Any {

	@Property(name = "iterator")
	@MethodSignature( returnSignature = "lense.core.collections.Iterator<T>", paramsSignature = "")
	public Iterator getIterator();

}

