package lense.core.math;

import java.math.BigInteger;

import lense.core.collections.NativeNaturalProgression;
import lense.core.collections.Progression;
import lense.core.lang.Ordinal;
import lense.core.lang.java.NonNull;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.reflection.Type;
import lense.core.lang.reflection.TypeResolver;

@PlatformSpecific
public interface Natural extends Whole , Ordinal {


	public static final TypeResolver TYPE_RESOLVER = TypeResolver.lazy(() -> new Type(Natural.class));
	

    public default @NonNull Progression upTo(@NonNull Natural other){
        return new NativeNaturalProgression(this, other);
    }
    
    public default @NonNull Progression upToExclusive(@NonNull Natural other){
        return new NativeNaturalProgression(this, other.predecessor());
    }
    
    public abstract Natural wholeDivide (Natural other);
    
    public default Integer wholeDivide(Integer other) {
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
    public default @NonNull Whole minus(@NonNull Whole other) {
    	return this.asInteger().minus(other);
    }
    
	public default Natural wrapPlus(Natural other) {
		return this.plus(other);
	}

	public abstract Natural wrapMinus(Natural other);
	
	public default Natural wrapMultiply(Natural other) {
		return this.multiply(other);
	}
	
    public default @NonNull Integer minus(@NonNull Natural other) {
        return this.asInteger().minus(other.asInteger());
    }

    public default @NonNull Integer symmetric() {
        return asInteger().symmetric();
    }

    public default boolean isLessThen(@NonNull Natural other) {
        return  compareTo(other) < 0;
    }

    public default boolean isLessOrEqualTo(@NonNull Natural other) {
        return  compareTo(other) <= 0;
    }

    public abstract @NonNull Natural successor();
    public abstract @NonNull Natural predecessor();

    public abstract boolean isZero();

    public abstract boolean isOne();
    
    public abstract boolean isPositive();

    public default boolean isNegative() {
    	return false;
    }
    
    public abstract @NonNull Natural multiply(@NonNull Natural other);
    
    public default Real raiseTo( Real other){
        if (other.isZero()){
            if (other.isZero()){
                return Rational.ONE;
            }
            return Rational.ZERO;
        } else if (this.isOne()){
            return Rational.ONE;
        } else if (other.isOne()){
            return Rational.constructor(this.asInteger(), Int32.ONE);
        } 
        return  new BigNatural(asJavaBigInteger()).raiseTo(other);
    }

    public default Natural raiseTo( Natural other){
        if (this.isZero()){
            if (other.isZero()){
                return Natural64.ONE;
            }
            return this;
        } else if (this.isOne()){
            return Natural64.ONE;
        } else if (other.isZero()){
            return Natural64.ONE;
        } else if (other.isOne()){
            return this;
        } else if (other.compareTo(Int32.TWO) == 0){
            return  this.multiply(this);
        } else if (other.compareTo(Int32.THREE) == 0){
            return  this.multiply(this).multiply(this);
        }
        return new BigNatural(asJavaBigInteger()).raiseTo(other);
    }

    public abstract boolean isInInt32Range();

    public default Rational raiseTo( Integer other){
        if (this.isZero()){
            if (other.isZero()){
                return Rational.ONE;
            }
            return Rational.ZERO;
        } else if (this.isOne()){
            return Rational.ONE;
        } else if (other.isZero()){
            return Rational.ONE;
        } else if (other.isOne()){
            return Rational.constructor(this.asInteger(), Int32.ONE);
        }  else if (other.isNegative()){
            return Rational.constructor(Int32.ONE, this.raiseTo(other.abs()).asInteger());
        } else {
            return Rational.constructor( this.raiseTo(other.abs()).asInteger(), Int32.ONE);
        }
    }



    public default @NonNull Integer multiply(@NonNull Integer other) {
        return other.asInteger().multiply(this.asInteger());
    }

    @Override
    public default @NonNull Natural abs() {
        return this;
    }
    
    public abstract Natural remainder(Natural n);
    

}

