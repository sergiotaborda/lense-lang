package lense.core.lang;

import lense.core.lang.java.Constructor;
import lense.core.lang.java.Signature;
import lense.core.math.Integer;

@Signature("[=T<lense.core.lang.Any]::")
public class Some extends Maybe{

	private Any value;
	
	@Constructor
	public static Some constructor(Any value){
		return new Some(value);
	}
	
	private Some(Any value){
		this.value =value;
	}

	@Override
	public boolean equalsTo(Any other) {
		return other instanceof Some && equalsTo((Some)other);
	}
	
	public boolean equalsTo(Some other) {
		return this.value.equalsTo(other.value);
	}

	@Override
	public HashValue hashValue() {
		return value.hashValue();
	}

	@Override
	public String asString() {
		return value.asString();
	}


	@Override
	public boolean isPresent() {
		return true;
	}

	@Override
	public boolean isAbsent() {
		return false;
	}
}
