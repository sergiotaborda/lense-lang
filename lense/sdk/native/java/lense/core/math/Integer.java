package lense.core.math;

import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.NonNull;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.java.ValueClass;

@ValueClass
public interface Integer extends Whole , Comparable, SignedNumber{

	@Constructor(paramsSignature = "")
	public static Integer constructor(){
		return Int32.valueOfNative(0);
	}

//	@Constructor(isImplicit = true, paramsSignature = "lense.core.math.Natural")
//	public static Integer valueOf(Natural n){
//		return Int32.valueOfNative(n.toPrimitiveInt());
//	}
//
//	@Constructor(isImplicit = true, paramsSignature = "lense.core.math.Whole")
//	public static Integer valueOf(Whole whole) {
//		if (whole instanceof Integer){
//			return (Integer) whole;
//		} else {
//			return valueOf((Natural)whole);
//		}
//	}

	public abstract Integer symmetric();

	public abstract Integer plus (Integer other);
	public abstract Integer multiply(Integer other);


	public default Integer minus (Natural other) {
		return minus(other.asInteger());
	}
	
	public default Integer plus (Natural other) {
		return plus(other.asInteger());
	}
	
	public default Integer multiply (Natural other) {
		return multiply(other.asInteger());
	}
	
	public abstract Integer minus(Integer other);
	
	
	public default Whole minus(Whole other) {
		return this.minus(other.asInteger());
	}
	
	public default Whole wrapMinus(Whole other) {
		return this.wrapMinus(other.asInteger());
	}
	
	public Integer wholeDivide(Integer other);
	
	public default Integer wholeDivide(Natural other) {
		return wholeDivide(other.asInteger());
	}
	
	@Override
	public default Whole plus(Whole other) {
		return this.plus(other.asInteger());
	}

	@PlatformSpecific
	public default Integer asInteger() {
		return this;
	}
	
	public abstract HashValue hashValue();

	public abstract Integer successor();
	public abstract Integer predecessor();

	
	public abstract Integer sign();

	public abstract boolean isZero();
    public abstract boolean isOne();

    public abstract boolean isNegative();

    public default @NonNull Integer raiseTo(Natural other) {
        if (this.isZero()){
            if (other.isZero()){
                return Int32.ONE;
            }
            return this;
        } else if (this.isOne()){
            return Int32.ONE;
        } else if (other.isZero()){
            return Int32.ONE;
        } else if (other.isOne()){
            return this;
        } else if (other.compareTo(Int32.valueOfNative(2)) == 0){
            return  this.multiply(this);
        } else if (other.compareTo(Int32.valueOfNative(3)) == 0){
            return  this.multiply(this).multiply(this);
        }
        return new BigInt(this.asJavaBigInteger()).raiseTo(other);
    }
    
    public default @NonNull Real raiseTo(Real other) {
        if (this.isZero()){
            if (other.isZero()){
                return Rational.ONE;
            }
            return Rational.constructor(this);
        } else if (this.isOne()){
            return Rational.ONE;
        } else if (other.isZero()){
            return Rational.ONE;
        } else if (other.isOne()){
            return Rational.constructor(this);
        } 
        return new BigDecimal(this.asJavaBigInteger()).raiseTo(other);
    }


    public abstract int toPrimitiveInt() ;
}
