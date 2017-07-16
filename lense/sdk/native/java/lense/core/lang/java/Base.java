package lense.core.lang.java;

import lense.core.lang.Any;
import lense.core.math.Int32;
import lense.core.math.Integer;

public class Base implements Any{

	@Override @Native
	public java.lang.String toString(){
		return this.asString().toString();
	}
	
	@Override @Native
	public final boolean equals(Object other){
		return other instanceof Any && super.equals(other);
	}
	
	@Override @Native
	public int hashCode(){
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
	public Integer hashValue() {
		return Int32.valueOfNative(super.hashCode());
	}
}
