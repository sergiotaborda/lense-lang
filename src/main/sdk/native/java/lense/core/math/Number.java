package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.String;
import lense.core.lang.java.Constructor;

public class Number implements lense.core.lang.TextRepresentable, Any{

	@Constructor
	public static Number constructor (){
		return new Number();
	}
	
	@Override
	public String asString() {
		return String.valueOfNative(this.toString());
	}

	@Override
	public Boolean equalsTo(Any other) {
		// TODO abstract
		return Boolean.FALSE;
	}

	@Override
	public Integer hashValue() {
		// TODO abstract
		return Int32.valueOfNative(0);
	}


}
