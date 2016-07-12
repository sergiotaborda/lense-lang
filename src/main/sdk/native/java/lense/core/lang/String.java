package lense.core.lang;

import lense.core.collections.Assortment;
import lense.core.collections.Iterator;
import lense.core.collections.NativeProgression;
import lense.core.collections.Progression;
import lense.core.collections.Sequence;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;
import lense.core.lang.java.Signature;
import lense.core.math.Int32;
import lense.core.math.Integer;
import lense.core.math.Natural;

@Signature("::lense.core.collections.Sequence<lense.core.lang.Character>")
public class String implements Sequence, TextRepresentable {

	@Constructor
	public static String constructor(){
		return new String("");
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
	
	@Native
	public java.lang.String toString() {
		return str;
	}
	
	public boolean equals(Object other){
		return other instanceof Any && equalsTo((Any)other).toPrimitiveBoolean();
	}
	
	public int hashCode(){
		return str.hashCode();
	}

	@Override
	public Boolean contains(Any other) {
		if (other instanceof Character){
			return Boolean.valueOfNative(str.indexOf(((Character)other).toPrimitiveChar()) >= 0);
		}
		return Boolean.FALSE;
	}

	@Override
	public Boolean containsAll(Assortment other) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Boolean getEmpty() {
		return Boolean.valueOfNative(str.isEmpty());
	}

	@Override
	public Boolean equalsTo(Any other) {
		return Boolean.valueOfNative(other instanceof String && ((String)other).str.equals(this.str));
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(this.str.hashCode());
	}

	public String plus(String other){
		return new String(this.str + other);
	}
	
	public String plus(TextRepresentable other){
		return new String(this.str + other.asString().str);
	}
}
