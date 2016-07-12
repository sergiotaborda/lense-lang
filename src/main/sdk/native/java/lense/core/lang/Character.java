package lense.core.lang;

import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;
import lense.core.math.Int32;
import lense.core.math.Integer;
import lense.core.math.Natural;

public final class Character implements Any{

	@Constructor
	public static Character constructor (Natural code){
		return new Character((char)code.toPrimitiveInt());
	}
	
	@Native
	public static Character valueOfNative (char code){
		return new Character(code);
	}

	private char code;
	
	private Character(char code){
		this.code = code;
	}
	
	
	public char toPrimitiveChar(){
		return code;
	}
	
	@Override
	public Boolean equalsTo(Any other) {
		return Boolean.valueOfNative(other instanceof Character && ((Character)other).code == this.code);
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(code);
	}
}
