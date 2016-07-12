package lense.core.io;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.TextRepresentable;
import lense.core.lang.java.Constructor;
import lense.core.math.Int32;
import lense.core.math.Integer;


public final class Console implements Any {

	private static Console Console = new Console();

	@Constructor
	public static Console constructor(){
		return Console;
	}
	
	public void print (TextRepresentable text){
		System.out.print(text.asString().toString());
	}
	
	public void println (TextRepresentable text){
		System.out.println(text.asString().toString());
	}
	
	@Override
	public Boolean equalsTo(Any other) {
		return Boolean.valueOfNative(other instanceof Console);
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(0);
	}
}
