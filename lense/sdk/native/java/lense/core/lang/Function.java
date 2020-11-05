package lense.core.lang;

import lense.core.lang.java.Native;
import lense.core.lang.java.Signature;

@Native
@Signature("[+R<lense.core.lang.Any, -T<lense.core.lang.Any]::")
public interface Function {

    public Any apply(Any argument);
}
