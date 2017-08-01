package lense.core.lang;

public interface Any {

	public boolean equalsTo(Any other);
	public HashValue hashValue ();
	public String asString();
}
