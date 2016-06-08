package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;

public class Int extends Integer {
	
	@Constructor
	public static Int constructor (){
		return new Int();
	}
	
	
	@Override @Native
	protected BigInteger getNativeBig() {
		// TODO Auto-generated method stub
		return null;
	}

}
