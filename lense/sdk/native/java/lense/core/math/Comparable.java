package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.java.MethodSignature;
import lense.core.lang.java.Signature;

@Signature("[=T<lense.core.lang.Any]::")
public interface Comparable extends Any {

	@MethodSignature( returnSignature = "lense.core.math.Comparison" , paramsSignature = "T",declaringType = "lense.core.math.Comparable")
	public Comparison compareWith (Any other); 
}
