package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;

public class BigInt extends Integer{

	@Constructor
	public static BigInt constructor (){
		return new BigInt();
	}
	
	private java.math.BigInteger value = java.math.BigInteger.ZERO;
	
	private BigInt(){
		
	}
	
	@Override @Native
	protected BigInteger getNativeBig() {
		return value;
	}

}
