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


	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public Integer plus(Integer n) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Integer minus(Integer n) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Integer multiply(Integer n) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Whole plus(Whole n) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Whole minus(Whole n) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Whole multiply(Whole n) {
		// TODO Auto-generated method stub
		return null;
	}

}
