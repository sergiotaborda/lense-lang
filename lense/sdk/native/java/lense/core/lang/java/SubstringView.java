package lense.core.lang.java;

import lense.core.lang.Any;
import lense.core.lang.Character;
import lense.core.lang.String;
import lense.core.math.NativeNumberFactory;
import lense.core.math.Natural;

public final class SubstringView extends String {

	private final String original;
	private final Natural start;
	private final Natural length;

	public SubstringView(String original, Natural start, Natural length) {
		this.original = original;
		this.start = start;
		this.length = length;
	}
	
	public java.lang.String toString() {
		return original.toString().substring(
				NativeNumberFactory.toPrimitiveInt(start),
				NativeNumberFactory.toPrimitiveInt(start.plus(length))
		);
	}
	

	@Override
	public Natural getSize() {
		return length;
	}

	@Override
	public Character get(Natural index) {
		return original.get(start.plus(index));
	}

	@Override
	public boolean contains(Any other) {
		var iterator = getIterator();
		while(iterator.moveNext()) {
			if (iterator.current().equals(other)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean getEmpty() {
		return length.isZero();
	}

	@Override
	public String concat(String other) {
		return ConcatenatedString.newInstance(this, other);
	}

	@Override
	public String subString(Natural start, Natural length) {
		return new SubstringView(original, this.start.plus(start), length);
	}

	@Override
	public boolean starstWith(String other) {
		if (other.getSize().compareWith(this.length).isGreater()) {
			return false;
		}
		
		return super.starstWith(other);
	}

	@Override
	public boolean endsWith(String other) {
		if (other.getSize().compareWith(this.length).isGreater()) {
			return false;
		}
		
		return super.endsWith(other);
	}

}
