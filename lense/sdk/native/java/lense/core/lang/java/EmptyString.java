package lense.core.lang.java;

import lense.core.lang.Any;
import lense.core.lang.Character;
import lense.core.lang.String;
import lense.core.math.Natural;
import lense.core.math.Natural64;

public class EmptyString implements String {

	@Override
	public Natural getSize() {
		return Natural64.ZERO;
	}

	@Override
	public Character get(Natural index) {
		return null;
	}

	@Override
	public boolean contains(Any other) {
		return false;
	}

	@Override
	public boolean getEmpty() {
		return true;
	}

	@Override
	public String concat(String other) {
		return other;
	}

}
