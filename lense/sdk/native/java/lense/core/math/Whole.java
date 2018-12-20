package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.Any;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.java.Signature;

@Signature(":lense.core.math.Number:lense.core.math.Comparable<lense.core.math.Whole>")
public interface Whole extends Number , Comparable {

    public abstract Whole plus (Whole other);
    public abstract Whole minus (Whole other);

    public default Rational divide(Whole other){
        if (other.isZero()){
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
        }
        return Rational.constructor(this.asInteger(), other.asInteger());
    }

    public abstract Integer asInteger();
    
    public default Natural gcd(Whole other) {
        BigInteger gcd =  this.asJavaBigInteger().gcd(other.asJavaBigInteger());
        
        if (gcd.bitLength() < 63){
            return new Natural64(gcd.longValue());
        } else {
            return new BigNatural(gcd);
        }
    }

    public abstract Whole successor();
    public abstract Whole predecessor();

    public abstract boolean isZero();
    public abstract boolean isOne();


    @Override
    public default boolean equalsTo(Any other) {
    	if (other instanceof Whole) {
    		return ((Whole)other).asJavaBigInteger().compareTo(this.asJavaBigInteger()) == 0;
    	} else if (other instanceof Real) {
    		return ((Real)other).equalsTo(Rational.constructor(this.asInteger(), Int32.ONE));
    	} 
    	return false;
      
    }


    @Override
    public default Comparison compareWith(Any other) {
        int comp = compareTo(((Whole)other));
        if (comp > 0){
            return Greater.GREATEAR;
        } else if (comp < 0){
            return Smaller.SMALLER;
        } else{
            return Equal.EQUAL;
        }
    }
    
    @PlatformSpecific
    public default int compareTo(Whole other) {
        return  this.asJavaBigInteger().compareTo(((Whole)other).asJavaBigInteger());
    }

    @PlatformSpecific
    public default BigInteger asJavaBigInteger() {
    	return new BigInteger(this.toString());
    }

    public abstract Natural abs();


}