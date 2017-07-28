package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;

public abstract class Integer extends Whole implements Comparable{

	@Constructor
	public static Integer constructor(){
		return Int32.valueOfNative(0);
	}

	@Constructor(isImplicit = true)
	public static Integer valueOf(Natural n){
		return Int32.valueOfNative(n.toPrimitiveInt());
	}

	@Constructor(isImplicit = true)
	public static Integer valueOf(Whole whole) {
		if (whole instanceof Integer){
			return (Integer) whole;
		} else {
			return valueOf((Natural)whole);
		}
	}

	public abstract Integer symmetric();

	public static final Integer ZERO = Integer.valueOfNative(0);
	public static final Integer ONE =  Integer.valueOfNative(1);

	public abstract Integer plus (Integer other);
	public abstract Integer multiply(Integer predecessor);

	public abstract Integer minus(Integer other);
	
	public final Whole minus(Whole other) {
		return this.minus(other.asInteger());
	}
	
	public Rational divide(Integer other){
		return super.divide(other);
	}
	
	public static Integer valueOfNative(int n) {
		return ScalableInt32.valueOf(n);
	}
	
	public static Integer valueOfNative(long n) {
		return ScalableInt64.valueOf(n);
	}
	
	public static Integer valueOfNative(String n) {
		return valueOf(new BigInteger(n));
	}
	
	public static Integer valueOf(BigInteger n) {

		if (n.bitLength() <= 32){
			return ScalableInt32.valueOf(n.intValue());
		} else if (n.bitLength() <= 64){
			return ScalableInt64.valueOf(n.longValue());
		}
		return new BigInt(n);
	}
	

	public final boolean isLessThen(Integer other) {
		return 	 compareTo(other) < 0;
	}
	
	@Override
	public Whole plus(Whole other) {
		return this.plus(other.asInteger());
	}

	@Override
	public Whole multiply(Whole other) {
		return this.multiply(other.asInteger());
	}
	
	@Override
	protected Integer asInteger() {
		return this;
	}
	
	public final Integer hashValue(){
		return this;
	}

	public abstract Integer successor();
	public abstract Integer predecessor();

	
	public abstract Integer signum();

	public abstract boolean isZero();
    public abstract boolean isOne();

    @Native
    public abstract Int32 toInt32();

}
