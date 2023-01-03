package lense.core.io;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;


public final class Console extends Base implements Any {

	private static Console Console = new Console();

    @Constructor(paramsSignature = "")
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
	public HashValue hashValue() {
		return new HashValue(0);
	}


}
