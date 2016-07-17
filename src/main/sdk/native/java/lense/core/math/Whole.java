package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.java.Constructor;

public abstract class Whole extends Number implements Comparable{

	@Constructor
	public static Whole constructor(){
		return Natural.valueOfNative(0);
	}

	public abstract Whole plus (Whole other);
	public abstract Whole minus (Whole other);
	public abstract Whole multiply(Whole other);
	
	public abstract Whole successor();

	public abstract boolean isZero();

	public abstract boolean isOne();

	public abstract Whole predecessor();

	@Override
	public Boolean equalsTo(Any other) {
		return Boolean.valueOfNative(other instanceof Whole && ((Whole)other).asBigInteger().compareTo(this.asBigInteger()) == 0);
	}


	@Override
	public Comparison compareTo(Any other) {
		int comp = this.compareTo((Whole)other);
		if (comp > 0){
			return Greater.constructor();
		} else if (comp < 0){
			return Smaller.constructor();
		} else{
			return Equal.constructor();
		}
	}

	@Override
	public Integer hashValue() {
		return BigInt.valueOf(asBigInteger());
	}
	
	protected abstract BigInteger asBigInteger();
	
	public abstract Natural abs();
	
	protected abstract Integer asInteger();
	
	public final boolean equals(Object other){
		return other instanceof Whole && this.compareTo((Whole)other) ==0;
	}
	
	public final int compareTo(Whole other){
		return this.asBigInteger().compareTo(other.asBigInteger());
	}
}