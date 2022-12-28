package lense.core.lang.java;

import lense.core.lang.Any;
import lense.core.lang.Character;
import lense.core.lang.HashValue;
import lense.core.lang.Maybe;
import lense.core.lang.None;
import lense.core.lang.Some;
import lense.core.lang.String;
import lense.core.lang.reflection.Type;
import lense.core.lang.reflection.TypeResolver;
import lense.core.math.NativeNumberFactory;
import lense.core.math.Natural;
import lense.core.math.Natural64;

public class NativeString implements String {

	public static final String EMPTY = new EmptyString();
	public static final TypeResolver TYPE_RESOLVER = TypeResolver.lazy(() -> Type.forClass(String.class));
	
	private java.lang.String str;
	
	public static String valueOfNative(java.lang.String str){
		if (str.isEmpty()) {
			return EMPTY;
		}
		return new NativeString(str);
	}
	
	
	
	@PlatformSpecific
	private NativeString(java.lang.String str){
		this.str = str;
	}
	
	public boolean equalsNative(java.lang.String nativeString) {
		return this.str.equals(nativeString);
	}
	
	public boolean equalsNative(NativeString nativeString) {
		return this.str.equals(nativeString.str);
	}
	
	@Override
	public Natural getSize() {
		return Natural64.valueOfNative(str.length());
	}

	@Override
	public Character get(Natural index) {
		return Character.valueOfNative(str.charAt(NativeNumberFactory.toPrimitiveInt(index)));
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
	public boolean getEmpty() {
		return str.isEmpty();
	}

	public boolean equals(Object other) {
		return  other instanceof Any that && this.equalsTo(that);
	}
	
	public int hashCode() {
		return hashValue().hashCode();
	}
	
	@Override
	public HashValue hashValue() {
		return new HashValue(this.str.hashCode());
	}
	

	@Override
	public String concat(String other) {
		return ConcatenatedString.newInstance(this, other);
	}

	@Override
	public String concat(java.lang.String other) {
		return new NativeString(this.str + other);
	}

	@Override
	public int length() {
		return this.str.length();
	}

	@Override
	public char charAt(int index) {
		return this.str.charAt(index);
	}


	@Override
	public String removeAt(Natural position) {
		return new NativeString(
				this.str.substring(0, NativeNumberFactory.naturalToPrimitiveInt(position))
				+ this.str.substring(NativeNumberFactory.naturalToPrimitiveInt(position) + 1)
		);
	}

	@Override
	public Maybe indexOf(String candidate) {
		if (candidate instanceof NativeString nativeString ) {
			int pos = this.str.indexOf(((NativeString)candidate).str);
			
			if (pos < 0) {
				return None.constructor();
			}
			
			return Some.constructor(JavaReifiedArguments.getInstance().addType(NativeNumberFactory.NATURAL_TYPE_RESOLVER), Natural64.valueOfNative(pos));
		}
		
		return String.super.indexOf(candidate);	
	}

	@Override
	public boolean starstWith(String other) {
		if (other instanceof NativeString nativeString ) {
			return str.startsWith(nativeString.str);
		}
		return indexOf(other).valueEqualsTo(Natural64.ZERO);
	}

	@Override
	public boolean endsWith(String other) {
		if (other instanceof NativeString nativeString ) {
			return str.endsWith(nativeString.str);
		}
		return indexOf(other).valueEqualsTo(this.getSize().minus(other.getSize()).abs());
	}

}
