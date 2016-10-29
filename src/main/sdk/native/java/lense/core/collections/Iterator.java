package lense.core.collections;

import lense.core.lang.java.Signature;

@Signature("[+T<lense.core.lang.Any]::")
public interface Iterator {

	public boolean hasNext();
	public lense.core.lang.Any next();
}
