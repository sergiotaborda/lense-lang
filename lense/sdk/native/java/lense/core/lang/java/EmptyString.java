package lense.core.lang.java;

import lense.core.lang.Any;
import lense.core.lang.Character;
import lense.core.lang.HashValue;
import lense.core.lang.String;
import lense.core.math.Natural;
import lense.core.math.Natural64;

public final class EmptyString extends String {

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

	@Override
	public boolean starstWith(String other) {
		return other.isEmpty();
	}

	@Override
	public boolean endsWith(String other) {
		return other.isEmpty();
	}

	public boolean equalsTo(Any other) {
		return  other instanceof String that && that.isEmpty();
	}
	
	public final HashValue hashValue() {
		return HashValue.constructor();
	}

	@Override
	public java.lang.String toString() {
		return "";
	}

	
	@Override
	public boolean equalsNative(java.lang.String nativeString) {
		return nativeString.isEmpty();
	}
}
