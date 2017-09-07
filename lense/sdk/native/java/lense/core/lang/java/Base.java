package lense.core.lang.java;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.reflection.Type;

@PlataformSpecific
public class Base implements Any{

	@Override @Native
	public java.lang.String toString(){
		return this.asString().toString();
	}
	
	@Override @Native
	public final boolean equals(Object other){
		return other instanceof Any && this.equalsTo((Any)other);
	}
	
	@Override @Native
	public  int hashCode(){
		return this.hashValue().hashCode();
	}
	
	public lense.core.lang.String asString(){
		return lense.core.lang.String.valueOfNative(super.toString());
	}

	@Override
	public boolean equalsTo(Any other) {
		return super.equals(other);
	}

	@Override
	public HashValue hashValue() {
		return new HashValue(super.hashCode());
	}

    @Override
    public Type type() {
        return new Type(this.getClass());
    }
}
