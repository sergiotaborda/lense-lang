package lense.core.lang;

import lense.core.lang.java.MethodSignature;
import lense.core.lang.java.Signature;
import lense.core.lang.java.TypeClass;

@Signature("[=A<lense.core.lang.Any,=D<lense.core.lang.Any,=S<lense.core.lang.Any]::")
@TypeClass 
public interface Concatenable {
	
	@MethodSignature( returnSignature = "S" , paramsSignature = "A,D")
	Any concatenate(Any a , Any b);
}