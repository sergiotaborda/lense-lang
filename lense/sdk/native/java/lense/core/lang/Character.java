package lense.core.lang;

import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;
import lense.core.math.Natural;

public final class Character extends Base implements Any, Ordinal{

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
	public boolean equalsTo(Any other) {
		return other instanceof Character && ((Character)other).code == this.code;
	}

	@Override
	public HashValue hashValue() {
		return new HashValue(code);
	}

	@Override
	public String asString() {
		return String.valueOfNative(java.lang.Character.toString(code));
	}

    @Override     
    public Object successor() {
     // TODO consider range from 0 to Int16.max
       return new Character(this.code++);
    }

    @Override
    public Object predecessor() {
        // TODO consider range from 0 to Int16.max
        return new Character(this.code--);
    }
}
