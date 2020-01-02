package lense.core.lang;

import lense.core.lang.java.Signature;

@Signature("[=T<lense.core.lang.Any]::")
public interface Ordinal extends Any {

    public Any successor();
    public Any predecessor();
}
