package lense.core.lang;

import lense.core.math.Natural;

public interface Binary {

	public Natural getSize ();
	
	// ~operator
	public Binary flipAll();
	
	// << operator
	public Binary rightShiftBy(Natural n);
	
	// >> operator
	public Binary leftShiftBy(Natural n);
}
