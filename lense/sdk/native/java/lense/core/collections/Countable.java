package lense.core.collections;

import lense.core.lang.java.MethodSignature;
import lense.core.lang.java.Property;
import lense.core.math.Natural;


public interface Countable {

	@Property(name = "size")
	@MethodSignature( returnSignature = "lense.core.math.Natural", paramsSignature = "")
	public Natural getSize();
	
	@Property(name = "empty")
	@MethodSignature( returnSignature = "lense.core.lang.Boolean", paramsSignature = "")
	public boolean getEmpty();
}
