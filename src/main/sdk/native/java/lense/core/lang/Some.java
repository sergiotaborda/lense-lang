package lense.core.lang;

import lense.core.lang.java.Constructor;

public class Some extends Maybe{

	private Any value;
	
	@Constructor
	public static Some constructor(Any value){
		return new Some(value);
	}
	
	private Some(Any value){
		this.value =value;
	}
}
