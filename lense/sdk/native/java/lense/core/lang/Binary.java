package lense.core.lang;

import lense.core.math.Natural;

public interface Binary extends ExclusiveDijunctable , Dijunctable , Injunctable {

	public Natural getSize ();
	
	// ~operator
	public Binary flipAll();
	
	// << operator
	public Binary rightShiftBy(Natural n);
	
	// >> operator
	public Binary leftShiftBy(Natural n);
	
	public boolean getBitAt(Natural index);
	
}
