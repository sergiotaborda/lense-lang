package lense.core.lang;

import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.MethodSignature;
import lense.core.lang.java.PlatformSpecific;
import lense.core.math.NativeNumberFactory;
import lense.core.math.Natural;


public final class Character extends Base implements Any, Ordinal{

	@Constructor(paramsSignature = "lense.core.math.Natural")
	public static Character constructor (Natural code){
		return new Character((char)NativeNumberFactory.naturalToPrimitiveInt(code));
	}
	
	@PlatformSpecific
	public static Character valueOfNative (char code){
		return new Character(code);
	}

	private char code;
	
	private Character(char code){
		this.code = code;
	}
	
	@PlatformSpecific
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
    @MethodSignature(returnSignature = "lense.core.lang.Character" , paramsSignature="", declaringType = "lense.core.lang.Ordinal" , overloaded = true)
    public Character successor() {
     // TODO consider range from 0 to Int16.max
       return new Character(this.code++);
    }

    @Override
    @MethodSignature(returnSignature = "lense.core.lang.Character" , paramsSignature="", declaringType = "lense.core.lang.Ordinal" , overloaded = true)
    public Character predecessor() {
        // TODO consider range from 0 to Int16.max
        return new Character(this.code--);
    }
}
