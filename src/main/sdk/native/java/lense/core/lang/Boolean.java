package lense.core.lang;

import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;
import lense.core.math.Int32;
import lense.core.math.Integer;

public class Boolean implements Any{

	public static Boolean TRUE = new Boolean(true);
	public static Boolean FALSE = new Boolean(false);
	
	private boolean value;

	@Native
	public static Boolean valueOfNative(boolean value) {
		return value ? TRUE : FALSE;
	}
	
	@Constructor
	public static Boolean constructor(){
		return FALSE;
	}
	
	@Native
	private Boolean(boolean value){
		this.value = value;
	}
	
	@Native
	public boolean toPrimitiveBoolean() {
		return value;
	}
	
	
	public Boolean negate(){
		return this.value ? FALSE : TRUE;
	}
	
	public Boolean flipAll(){
		return this.value ? FALSE : TRUE;
	}

	@Native
	public java.lang.String toString(){
		return java.lang.Boolean.toString(value);
	}

	@Override
	public Boolean equalsTo(Any other) {
		return Boolean.valueOfNative(other instanceof Boolean && ((Boolean)other).value == this.value);
	}

	@Override
	public Integer hashValue() {
		return value ? Int32.valueOfNative(1) :Int32.valueOfNative(0);
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
