package lense.core.lang;

import lense.core.collections.Assortment;
import lense.core.collections.Iterator;
import lense.core.collections.NativeProgression;
import lense.core.collections.Progression;
import lense.core.collections.Sequence;
import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;
import lense.core.lang.java.Signature;
import lense.core.math.Int32;
import lense.core.math.Integer;
import lense.core.math.Natural;

@Signature("::lense.core.collections.Sequence<lense.core.lang.Character>")
public class String extends Base implements Sequence {

	public static final String EMPTY = new String("");

	@Constructor
	public static String constructor(){
		return EMPTY;
	}
	
	@Native
	public static String valueOfNative(java.lang.String str){
		return new String(str);
	}
	
	private java.lang.String str;
	
	@Native
	private String(java.lang.String str){
		this.str = str;
	}
	
	@Override
	public Natural getSize() {

		return Natural.valueOfNative(str.length());
	}

	@Override
	public Iterator getIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public lense.core.lang.Character get(Natural index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Progression getIndexes() {
		return new NativeProgression(0, this.str.length());
	}

	@Override
	public String asString() {
		return this;
	}
	
	@Override
	public java.lang.String toString() {
		return str;
	}

	@Override
	public boolean contains(Any other) {
		if (other instanceof Character){
			return str.indexOf(((Character)other).toPrimitiveChar()) >= 0;
		}
		return false;
	}

	@Override
	public boolean containsAll(Assortment other) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty() {
		return str.isEmpty();
	}

	@Override
	public boolean equalsTo(Any other) {
		return other instanceof String && ((String)other).str.equals(this.str);
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(this.str.hashCode());
	}

	public String plus(String other){
		return new String(this.str + other.str);
	}
	
	@Native
	public String plus(java.lang.String other){
		return new String(this.str + other);
	}
	
	public String plus(Any other){
		if (other == null){
			throw new IllegalArgumentException("argument cannot be null");
		}
		if (other.asString() == null){
			throw new IllegalArgumentException("asString cannot be null");
		}
		return new String(this.str + other.asString().str);
	}


}
