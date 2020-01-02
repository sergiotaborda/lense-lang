package lense.core.lang.java;

import lense.core.lang.Any;
import lense.core.lang.Ordinal;

public interface JavaOrdinal<T extends Any> extends Ordinal {

    public T successor();
    public T predecessor();
}
