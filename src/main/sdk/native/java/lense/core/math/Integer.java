package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;

public abstract class Integer extends Whole{

	@Constructor
	public static Integer constructor(){
		return Int32.valueOfNative(0);
	}
	
	public Int32 compareTo(Integer other){
		return Int32.valueOfNative(this.getNativeBig().compareTo(other.getNativeBig()));
	}
	
	@Native
	protected abstract BigInteger getNativeBig();
}
