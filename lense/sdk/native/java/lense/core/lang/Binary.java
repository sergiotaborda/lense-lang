package lense.core.lang;

import lense.core.lang.java.Property;
import lense.core.lang.java.Signature;
import lense.core.math.Natural;

@Signature("::lense.core.lang.ExclusiveDijunctable<lense.core.lang.Binary>&lense.core.lang.Dijunctable<lense.core.lang.Binary>&lense.core.lang.Injunctable<lense.core.lang.Binary>")
public interface Binary extends ExclusiveDijunctable , Dijunctable , Injunctable {

    @Property(name = "bitsCount")
	public Natural bitsCount ();
	
	// ~operator
	public Binary complement();
	
	// << operator
	public Binary rightShiftBy(Natural n);
	
	// >> operator
	public Binary leftShiftBy(Natural n);
	
	public boolean bitAt(Natural index);
	
}
