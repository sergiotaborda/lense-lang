package lense.core.math;

import java.math.BigInteger;

import lense.core.collections.NativeNaturalProgression;
import lense.core.collections.Progression;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.NonNull;
import lense.core.lang.java.PlatformSpecific;

public abstract class Natural extends Whole   {

    public static final Natural ONE = Natural.valueOfNative(1);
    public static final Natural ZERO = Natural.valueOfNative(0);

    @Constructor(paramsSignature = "")
    public static Natural constructor(){
        return Natural.valueOfNative(0);
    }

    @Constructor(paramsSignature = "lense.core.lang.String")
    public static Natural parse(lense.core.lang.String text){
        return valueOf(text.toString());
    }

    @PlatformSpecific
    public static Natural valueOfNative(int value){
        if (value < 0){
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("A negative integer cannot be transformed to a Natural"));
        }
        return new UNat(value);
    }

    @PlatformSpecific
    public static Natural valueOfNative(long value) {
        if (value < 0){
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("A negative integer cannot be transformed to a Natural"));
        }
        return new UNat(value);
    }

    public static Natural valueOf(String n) {
        return valueOf(new BigInteger(n));
    }

    public static Natural valueOf(BigInteger n) {
        if (n.signum() < 0){
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("A negative integer cannot be transformed to a Natural"));
        }
        if (n.compareTo(new BigInteger("18446744073709551615")) <= 0){
            return new UNat(n.toString());
        } 
        return new BigNatural(n);
    }

    public @NonNull Progression upTo(@NonNull Natural other){
        return new NativeNaturalProgression(this, other);
    }
    
    public @NonNull Progression upToExclusive(@NonNull Natural other){
        return new NativeNaturalProgression(this, other.predecessor());
    }
    
    public abstract Natural wholeDivide (Natural other);
    
    public Integer wholeDivide(Integer other) {
        BigInteger div = asJavaBigInteger().divide(other.asJavaBigInteger());
        
        if (div.bitLength() < 32){
            return new Int32(div.intValue());
        } else if (div.bitLength() < 64){
            return new Int64(div.longValue());
        }
        return new BigInt(div);
    }
    
    @PlatformSpecific
    public abstract int toPrimitiveInt();

    public abstract int modulus(int n);

    public abstract @NonNull Natural plus (@NonNull Natural other);

    @Override
    public final @NonNull Whole minus(@NonNull Whole other) {
    	return this.asInteger().minus(other);
    }
    
	public Natural wrapPlus(Natural other) {
		return this.plus(other);
	}

	public abstract Natural wrapMinus(Natural other);
	public Natural wrapMultiply(Natural other) {
		return this.multiply(other);
	}
	
    public final @NonNull Integer minus(@NonNull Natural other) {
        return this.asInteger().minus(other.asInteger());
    }

    public final @NonNull Integer symmetric() {
        return asInteger().symmetric();
    }

    public final boolean isLessThen(@NonNull Natural other) {
        return  compareTo(other) < 0;
    }

    public final boolean isLessOrEqualTo(@NonNull Natural other) {
        return  compareTo(other) <= 0;
    }

    public abstract @NonNull Natural successor();
    public abstract @NonNull Natural predecessor();

    public abstract boolean isZero();

    public abstract boolean isOne();
    
    public abstract boolean isPositive();

    public boolean isNegative() {
    	return false;
    }
    
    public abstract @NonNull Natural multiply(@NonNull Natural other);
    
    public Real raiseTo( Real other){
        if (other.isZero()){
            if (other.isZero()){
                return Real.ONE;
            }
            return Real.ZERO;
        } else if (this.isOne()){
            return Real.ONE;
        } else if (other.isOne()){
            return Rational.constructor(this.asInteger(), Integer.ONE);
        } 
        return this.asBigNat().raiseTo(other);
    }

    public Natural raiseTo( Natural other){
        if (this.isZero()){
            if (other.isZero()){
                return Natural.ONE;
            }
            return this;
        } else if (this.isOne()){
            return Natural.ONE;
        } else if (other.isZero()){
            return Natural.ONE;
        } else if (other.isOne()){
            return this;
        } else if (other.compareTo(Integer.valueOfNative(2)) == 0){
            return  this.multiply(this);
        } else if (other.compareTo(Integer.valueOfNative(3)) == 0){
            return  this.multiply(this).multiply(this);
        }
        return this.asBigNat().raiseTo(other);
    }

    protected BigNatural asBigNat(){
        return new BigNatural(this.asJavaBigInteger());
    }

    public abstract boolean isInInt32Range();

    public Rational raiseTo( Integer other){
        if (this.isZero()){
            if (other.isZero()){
                return Real.ONE;
            }
            return Rational.ZERO;
        } else if (this.isOne()){
            return Rational.ONE;
        } else if (other.isZero()){
            return Rational.ONE;
        } else if (other.isOne()){
            return Rational.constructor(this.asInteger(), Integer.ONE);
        }  else if (other.isNegative()){
            return Rational.constructor(Integer.ONE, this.raiseTo(other.abs()).asInteger());
        } else {
            return Rational.constructor( this.raiseTo(other.abs()).asInteger(), Integer.ONE);
        }
    }

    @Override
    public @NonNull Whole plus(@NonNull Whole other) {
        if (other instanceof Natural){
            return this.plus((Natural)other);
        } else {
            return this.asInteger().plus(other.asInteger());
        }
    }

    public @NonNull Integer multiply(@NonNull Integer other) {
        return other.asInteger().multiply(this.asInteger());
    }

    @Override
    public final @NonNull Natural abs() {
        return this;
    }
    
    public abstract Natural remainder(Natural n);
}

