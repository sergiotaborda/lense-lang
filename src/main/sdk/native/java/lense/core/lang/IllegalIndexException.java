package lense.core.lang;

import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;

public class IllegalIndexException extends lense.core.lang.Exception {

	@Native
	private static final long serialVersionUID = -2087029916407335704L;

	@Constructor
	public static IllegalIndexException constructor(){
		return new IllegalIndexException();
	}
	
	protected IllegalIndexException(){}
}
