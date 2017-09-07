package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.java.Signature;

@Signature("[=T<lense.core.lang.Any]::")
public interface Comparable extends Any {

	public Comparison compareWith (Any other); 
}
