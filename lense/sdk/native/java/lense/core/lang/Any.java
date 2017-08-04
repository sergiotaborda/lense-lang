package lense.core.lang;

import lense.core.lang.reflection.Type;

public interface Any {

	public boolean equalsTo(Any other);
	public HashValue hashValue ();
	public String asString();
	public Type type();
}
