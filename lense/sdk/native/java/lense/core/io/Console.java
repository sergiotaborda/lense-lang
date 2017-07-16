package lense.core.io;

import lense.core.lang.Any;
import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;
import lense.core.math.Int32;
import lense.core.math.Integer;


public final class Console extends Base implements Any {

	private static Console Console = new Console();

	@Constructor
	public static Console constructor(){
		return Console;
	}
	
	public void print (Any text){
		System.out.print(text.asString().toString());
	}
	
	public void println (Any text){
		System.out.println(text.asString().toString());
	}
	
	@Override
	public boolean equalsTo(Any other) {
		return other instanceof Console;
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(0);
	}


}
