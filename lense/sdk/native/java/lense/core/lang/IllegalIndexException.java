package lense.core.lang;

import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;
import lense.core.lang.java.PlatformSpecific;

public class IllegalIndexException extends lense.core.lang.Exception {

	@PlatformSpecific
	private static final long serialVersionUID = -2087029916407335704L;

	@Constructor
	public static IllegalIndexException constructor(){
		return new IllegalIndexException();
	}
	
	protected IllegalIndexException(){}
}
