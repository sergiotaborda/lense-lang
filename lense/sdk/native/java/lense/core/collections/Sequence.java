package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.java.MethodSignature;
import lense.core.lang.java.Property;
import lense.core.lang.java.Signature;
import lense.core.math.Natural;


@Signature("[+T<lense.core.lang.Any]::lense.core.collections.Assortment<T>")
public interface Sequence extends Assortment {

	@Property(indexed = true)
	@MethodSignature(returnSignature = "T", paramsSignature = "_")
	public Any get(Natural index);
	
	@Property(name = "indexes")
	@MethodSignature(returnSignature = "lense.core.collections.Progression<T>", paramsSignature = "")
	public Progression getIndexes();
}
