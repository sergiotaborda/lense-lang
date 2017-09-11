package lense.core.lang;

import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;
import lense.core.lang.java.PlatformSpecific;


public class Boolean extends Base implements Any{

	public static Boolean TRUE = new Boolean(true);
	public static Boolean FALSE = new Boolean(false);
	
	private boolean value;

	@PlatformSpecific
	public final static Boolean valueOfNative(boolean value) {
		return value ? TRUE : FALSE;
	}
	
	@Constructor
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
	
	
	public Boolean negate(){
		return this.value ? FALSE : TRUE;
	}
	
	public Boolean flipAll(){
		return this.value ? FALSE : TRUE;
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

	public Boolean and(Boolean other) {
		return this.value && other.value ? TRUE : FALSE;
	}

	public Boolean or(Boolean other) {
		return this.value || other.value ? TRUE : FALSE;
	}
	
	public Boolean xor(Boolean other) {
		return this.value ^ other.value ? TRUE : FALSE;
	}



}
