package lense.core.lang;

import lense.core.math.Integer;

public interface Any {

	public boolean equalsTo(Any other);
	public Integer hashValue ();
	public String asString();
}
