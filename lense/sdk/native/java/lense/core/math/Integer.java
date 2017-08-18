package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;
import lense.core.lang.java.NonNull;

public abstract class Integer extends Whole implements Comparable, SignedNumber{

	public static @NonNull Integer INT_MAX = Integer.valueOfNative(java.lang.Integer.MAX_VALUE);
	public static @NonNull Integer INT_MIN = Integer.valueOfNative(java.lang.Integer.MIN_VALUE);

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
	
	@Native
	public static Integer valueOfNative(int n) {
		return ScalableInt32.valueOf(n);
	}
	@Native
	public static Integer valueOfNative(long n) {
		return ScalableInt64.valueOf(n);
	}
	@Native
	public static Integer valueOfNative(String n) {
		return valueOfNative(new BigInteger(n));
	}
	@Native
	public static Integer valueOfNative(BigInteger n) {

		if (n.bitLength() <= 32){
			return ScalableInt32.valueOf(n.intValue());
		} else if (n.bitLength() <= 64){
			return ScalableInt64.valueOf(n.longValue());
		}
		return new BigInt(n);
	}
	

	public final boolean isLessThen(@NonNull Integer other) {
		return 	 compareTo(other) < 0;
	}
	
    public final boolean isLessOrEqualTo(@NonNull Integer other) {
        return  compareTo(other) <= 0;
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
	
	public abstract HashValue hashValue();

	public abstract Integer successor();
	public abstract Integer predecessor();

	
	public abstract Integer signum();

	public abstract boolean isZero();
    public abstract boolean isOne();

    @Native
    public abstract Int32 toInt32();

    public abstract boolean isNegative();

    public @NonNull Integer raiseTo(Natural other) {
        if (this.isZero()){
            if (other.isZero()){
                return Integer.ONE;
            }
            return this;
        } else if (this.isOne()){
            return Integer.ONE;
        } else if (other.isZero()){
            return Integer.ONE;
        } else if (other.isOne()){
            return this;
        } else if (other.compareTo(Integer.valueOfNative(2)) == 0){
            return  this.multiply(this);
        } else if (other.compareTo(Integer.valueOfNative(3)) == 0){
            return  this.multiply(this).multiply(this);
        }
        return new BigInt(this.asBigInteger()).raiseTo(other);
    }

    public Integer wholeDivide (Integer other){
        if (other.isZero()){
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
        }  
        return Integer.valueOfNative(this.asBigInteger().divide(other.asBigInteger()));
    }

    public Integer remainder (Integer other){
        if (other.isZero()){
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
        }  
        return Integer.valueOfNative(this.asBigInteger().remainder(other.asBigInteger()));
    }

    public abstract int toPrimitiveInt() ;
}
