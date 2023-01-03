package lense.core.lang;

import lense.core.lang.java.MethodSignature;
import lense.core.lang.java.Signature;
import lense.core.lang.java.TypeClass;

@Signature("[=A<lense.core.lang.Any,=D<lense.core.lang.Any,=S<lense.core.lang.Any]::")
@TypeClass 
public interface Summable {
	
	@MethodSignature( returnSignature = "S" , paramsSignature = "A,D")
	Any sum(Any a , Any b);
}