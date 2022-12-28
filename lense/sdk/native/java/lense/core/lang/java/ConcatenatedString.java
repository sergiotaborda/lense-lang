package lense.core.lang.java;

import lense.core.collections.Iterator;
import lense.core.lang.Any;
import lense.core.lang.Character;
import lense.core.lang.String;
import lense.core.math.Natural;

@PlatformSpecific
public final class ConcatenatedString extends String {

	
	public static String newInstance(String a, String b) {
		if (a.isEmpty()) {
			return b;
		} else if (b.isEmpty()) {
			return a;
		}
		return new ConcatenatedString(a,b);
	}
	
	private final String start;
	private final String end;
	private final Natural length;

	private ConcatenatedString(String a, String b) {
		start = a;
		end = b;
		length = a.getSize().plus(b.getSize());
	}

	@Override
	public String concat(String other) {
		if (other.isEmpty()) {
			return this;
		} else if (this.isEmpty()) {
			return other;
		}
		return new ConcatenatedString(this, other);
	}

	
	@Override
	public Natural getSize() {
		return length;
	}
	
	
	@Override
	public java.lang.String toString() {
		StringBuilder builder = new StringBuilder();
		collectText(start, builder);
		collectText(end, builder);
		return builder.toString();
	}


	private void collectText(String value, StringBuilder builder) {
		 if (value.isEmpty()) {
			 return;
		 } else if (value instanceof ConcatenatedString concatenated) {
			 collectText(concatenated.start, builder);
			 collectText(concatenated.end, builder);
		 } else {
			 builder.append(value.toString());
		 }
	}

	@Override
	public boolean contains(Any other) {
		if (other instanceof Character c ){
			return start.contains(c) || end.contains(c);
		}
		return false;
	}
	
	@Override
	public Iterator getIterator() {
		return ComposedIterator.iterate(start.getIterator()).then(end.getIterator());
	}

	@Override
	public Character get(Natural index) {
		if (index.compareWith(start.getSize()).isSmaller()) {
			return start.get(index);
		}
		
		return end.get(index.minus(start.getSize()).abs());
	}

	@Override
	public boolean getEmpty() {
		 return length.isZero();
	}

	@Override
	public char charAt(int index) {
		if (index < start.length()) {
			return start.charAt(index);
		}
		
		return end.charAt(index - start.length());
	}

	@Override
	public String subString(Natural start, Natural length) {
		return new SubstringView(this, start, length);
	}

	@Override
	public boolean starstWith(String other) {
		if (other.length() <= start.length()) {
			return start.starstWith(other);
		}
	
		return super.starstWith(other);
	}

	@Override
	public boolean endsWith(String other) {
		if (other.length() <= end.length()) {
			return end.endsWith(other);
		}
		
		return super.endsWith(other);
	}
	
}
