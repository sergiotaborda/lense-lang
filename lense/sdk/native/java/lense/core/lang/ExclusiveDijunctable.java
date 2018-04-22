package lense.core.lang;

import lense.core.lang.java.MethodSignature;
import lense.core.lang.java.Signature;

@Signature("[+T<lense.core.lang.Any]::")
public interface ExclusiveDijunctable {

	@MethodSignature( returnSignature = "T", paramsSignature = "T")
    public Any xor(Any other);
}
