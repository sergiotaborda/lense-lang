package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.Maybe;
import lense.core.lang.java.Signature;

@Signature("[+K<lense.core.lang.Any,+V<lense.core.lang.Any]::lense.core.collections.Assortment<lense.core.collections.KeyPair<K,V>>")
public interface Association extends Assortment{

	public Maybe get(Any key);
}
