package lense.core.lang;

import lense.core.lang.java.Native;

@Native
public interface Function {

    public Any apply(Any argument);
}
