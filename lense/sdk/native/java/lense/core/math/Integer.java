package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.NonNull;
import lense.core.lang.java.PlatformSpecific;

public abstract class Integer extends Whole implements Comparable, SignedNumber{

	@Constructor(paramsSignature = "")
	public static Integer constructor(){
		return Int32.valueOfNative(0);
	}

	@Constructor(isImplicit = true, paramsSignature = "lense.core.math.Natural")
	public static Integer valueOf(Natural n){
		return new BigInt(n.asJavaBigInteger());
	}

	@Constructor(isImplicit = true, paramsSignature = "lense.core.math.Whole")
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
	
	
	public final Whole wrapMinus(Whole other) {
		return this.wrapMinus(other.asInteger());
	}
	
	public Rational divide(Integer other){
		return super.divide(other);
	}
	
	@PlatformSpecific
	public static Integer valueOfNative(int n) {
		return Int32.valueOfNative(n);
	}
	@PlatformSpecific
	public static Integer valueOfNative(long n) {
		return Int64.valueOfNative(n);
	}
	@PlatformSpecific
	public static Integer valueOfNative(String n) {
		return valueOfNative(new BigInteger(n));
	}
	@PlatformSpecific
	public static Integer valueOfNative(BigInteger n) {

		if (n.bitLength() <= 32){
			return Int32.valueOfNative(n.intValue());
		} else if (n.bitLength() <= 64){
			return Int64.valueOfNative(n.longValue());
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
	protected Integer asInteger() {
		return this;
	}
	
	public abstract HashValue hashValue();

	public abstract Integer successor();
	public abstract Integer predecessor();

	
	public abstract Integer signum();

	public abstract boolean isZero();
    public abstract boolean isOne();

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
        return new BigInt(this.asJavaBigInteger()).raiseTo(other);
    }
    
    public @NonNull Real raiseTo(Real other) {
        if (this.isZero()){
            if (other.isZero()){
                return Real.ONE;
            }
            return Real.valueOf(this);
        } else if (this.isOne()){
            return Real.ONE;
        } else if (other.isZero()){
            return Real.ONE;
        } else if (other.isOne()){
            return Real.valueOf(this);
        } 
        return new BigDecimal(this.asJavaBigInteger()).raiseTo(other);
    }
    
    

    public Integer wholeDivide ( Natural other) {
        if (other.isZero()){
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
        }  
        return Integer.valueOfNative(this.asJavaBigInteger().divide(other.asJavaBigInteger()));
    }
    
    public Integer wholeDivide (Integer other){
        if (other.isZero()){
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
        }  
        return Integer.valueOfNative(this.asJavaBigInteger().divide(other.asJavaBigInteger()));
    }

    public Integer remainder (Integer other){
        if (other.isZero()){
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
        }  
        return Integer.valueOfNative(this.asJavaBigInteger().remainder(other.asJavaBigInteger()));
    }

    public abstract int toPrimitiveInt() ;
}
