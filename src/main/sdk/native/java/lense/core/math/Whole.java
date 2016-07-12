package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.java.Constructor;

public abstract class Whole extends Number implements Comparable{

	@Constructor
	public static Whole constructor(){
		return Int32.valueOfNative(0);
	}

	public abstract Whole plus(Whole n);
	
	public abstract Whole minus(Whole n);
	
	public abstract Whole multiply(Whole n);
	
	public Rational divide(Whole other){
		return Rational.constructor(Integer.valueOf(this), Integer.valueOf(this));
	}
	
	abstract BigInteger getNativeBig();
}
