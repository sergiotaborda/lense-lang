package lense.core.math;

import lense.core.collections.Progression;
import lense.core.lang.Ordinal;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.NonNull;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.java.Signature;

@Signature("::lense.core.math.Whole&lense.core.math.Ordinal")
@PlatformSpecific
public interface Natural extends Whole , Ordinal , Progressable, Comparable {



	@Constructor(isImplicit = false, paramsSignature = "lense.core.lang.String")
	public static Natural parse(lense.core.lang.String text){
	    return BigNatural.parse(text).reduce();
	}

    public Natural wholeDivide (Natural other);
    
    public Integer wholeDivide(Integer other) ;

    public int modulus(int n);

    public Natural plus ( Natural other);

    public Whole minus( Whole other);
    
    public Natural wrapPlus(Natural other);
    
	public Natural wrapMinus(Natural other);
	
	public Natural wrapMultiply(Natural other);

	
    public Integer minus( Natural other);

    public  Integer symmetric();
    
    public @NonNull Natural successor();
    public @NonNull Natural predecessor();

    public boolean isZero();

    public boolean isOne();
    
    public boolean isPositive();

    public boolean isNegative();
    
    public @NonNull Natural multiply(@NonNull Natural other);
    
    public  Real raiseTo( Real other);
    
//    public default Real raiseTo( Real other){
//        if (other.isZero()){
//            if (other.isZero()){
//                return Rational.ONE;
//            }
//            return Rational.ZERO;
//        } else if (this.isOne()){
//            return Rational.ONE;
//        } else if (other.isOne()){
//            return Rational.constructor(this.asInteger(), Int32.ONE);
//        } 
//        return  new BigNatural(asJavaBigInteger()).raiseTo(other);
//    }

    public Natural raiseTo( Natural other);
    
//    public default Natural raiseTo( Natural other){
//        if (this.isZero()){
//            if (other.isZero()){
//                return Natural64.ONE;
//            }
//            return this;
//        } else if (this.isOne()){
//            return Natural64.ONE;
//        } else if (other.isZero()){
//            return Natural64.ONE;
//        } else if (other.isOne()){
//            return this;
//        } else if (other.compareTo(Int32.TWO) == 0){
//            return  this.multiply(this);
//        } else if (other.compareTo(Int32.THREE) == 0){
//            return  this.multiply(this).multiply(this);
//        }
//        return new BigNatural(asJavaBigInteger()).raiseTo(other);
//    }

//	public default boolean isInInt32Range() {
//		return  NativeNumberFactory.compareNumbers(this, Natural64.INT32_MAX) <=0;
//	}
	
    public Rational raiseTo( Integer other);
	
//    public default Rational raiseTo( Integer other){
//        if (this.isZero()){
//            if (other.isZero()){
//                return Rational.ONE;
//            }
//            return Rational.ZERO;
//        } else if (this.isOne()){
//            return Rational.ONE;
//        } else if (other.isZero()){
//            return Rational.ONE;
//        } else if (other.isOne()){
//            return Rational.constructor(this.asInteger(), Int32.ONE);
//        }  else if (other.isNegative()){
//            return Rational.constructor(Int32.ONE, this.raiseTo(other.abs()).asInteger());
//        } else {
//            return Rational.constructor( this.raiseTo(other.abs()).asInteger(), Int32.ONE);
//        }
//    }

    public  Integer multiply( Integer other);
    
//    public default @NonNull Integer multiply(@NonNull Integer other) {
//        return other.asInteger().multiply(this.asInteger());
//    }

    public  @NonNull Natural abs();
    
    public Natural remainder(Natural n);
    

}

