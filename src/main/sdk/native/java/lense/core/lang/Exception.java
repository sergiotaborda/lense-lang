package lense.core.lang;

import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;

public class Exception extends java.lang.RuntimeException {

	@Native
	private static final long serialVersionUID = 1L;

	@Constructor
	public static Exception constructor(){
		return new Exception();
	}
	
	protected Exception(){}
}
