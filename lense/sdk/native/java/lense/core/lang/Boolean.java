package lense.core.lang;

import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.PlatformSpecific;

// Must be already erased 
public class Boolean extends Base implements Any{

	public static Boolean TRUE = new Boolean(true);
	public static Boolean FALSE = new Boolean(false);
	
	private boolean value;

	@PlatformSpecific
	public final static Boolean valueOfNative(boolean value) {
		return value ? TRUE : FALSE;
	}
	
	@Constructor(paramsSignature = "")
	public static Boolean constructor(){
		return FALSE;
	}
	
	@PlatformSpecific
	private Boolean(boolean value){
		this.value = value;
	}
	
	@PlatformSpecific
	public boolean toPrimitiveBoolean() {
		return value;
	}
	
	
	public boolean negate(){
		return !this.value;
	}
	
	public boolean flipAll(){
		return !this.value;
	}

	@Override
	public String asString() {
		return String.valueOfNative(java.lang.Boolean.toString(value));
	}
	
	@Override
	public boolean equalsTo(Any other) {
		return other instanceof Boolean && ((Boolean)other).value == this.value;
	}

	@Override
	public HashValue hashValue() {
		return value ? new HashValue(1) : new HashValue(0);
	}

	public boolean and(boolean other) {
		return this.value && other;
	}

	public boolean or(boolean other) {
		return this.value || other;
	}
	
	public boolean xor(boolean other) {
		return this.value ^ other;
	}



}
