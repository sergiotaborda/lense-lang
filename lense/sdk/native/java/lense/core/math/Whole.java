package lense.core.math;

import java.math.BigInteger;

import lense.core.collections.NativeOrdinalProgression;
import lense.core.collections.Progression;
import lense.core.lang.Any;
import lense.core.lang.java.JavaOrdinal;
import lense.core.lang.java.Signature;

@Signature(":lense.core.math.Number:lense.core.math.Comparable<lense.core.math.Whole>")
public abstract class Whole extends Number implements Comparable , JavaOrdinal<Whole>{

    public Rational divide(Whole other){
        if (other.isZero()){
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
        }
        return Rational.constructor(this.asInteger(), other.asInteger());
    }

    public Whole wholeDivide (Whole other){
        if (other.isZero()){
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
        }  
        return Integer.valueOfNative(this.asJavaBigInteger().divide(other.asJavaBigInteger()));
    }
    
    public Natural gcd(Whole other) {
        BigInteger gcd =  this.asJavaBigInteger().gcd(other.asJavaBigInteger());
        
        if (gcd.bitLength() < 64){
            return new UNat(gcd.longValue());
        } else {
            return new BigNatural(gcd);
        }
    }


    public Whole remainder (Whole other){
        if (other.isZero()){
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
        }  
        return Integer.valueOfNative(this.asJavaBigInteger().remainder(other.asJavaBigInteger()));
    }


    public abstract Whole successor();
    public abstract Whole predecessor();

    public abstract boolean isZero();
    public abstract boolean isOne();


    @Override
    public boolean equalsTo(Any other) {
    	if (other instanceof Whole) {
    		return ((Whole)other).asJavaBigInteger().compareTo(this.asJavaBigInteger()) == 0;
    	} else if (other instanceof Real) {
    		return ((Real)other).equalsTo(Real.valueOf(this));
    	} 
    	return false;
      
    }


    @Override
    public Comparison compareWith(Any other) {
        int comp = this.compareTo((Whole)other);
        if (comp > 0){
            return Greater.constructor();
        } else if (comp < 0){
            return Smaller.constructor();
        } else{
            return Equal.constructor();
        }
    }

    public Progression upTo(Whole other){
        return new NativeOrdinalProgression(this, other, true);
    }
    
    public Progression upToExclusive(Whole other){
        return new NativeOrdinalProgression(this, other, false);
    }
    
    protected abstract BigInteger asJavaBigInteger();

    public abstract Natural abs();

    protected abstract Integer asInteger();

    protected final int compareTo(Whole other){
        return this.asJavaBigInteger().compareTo(other.asJavaBigInteger());
    }

    public Complex plus(Imaginary n ){
        return Complex.constructor(Real.valueOf(this), n.real());
    }

    public Complex minus(Imaginary n){
        return Complex.constructor(Real.valueOf(this), n.real().symmetric());
    }
    public Imaginary multiply(Imaginary n){
        return Imaginary.valueOf(Real.valueOf(this).multiply(n.real()));
    }
    public Imaginary divide(Imaginary n){
        return Imaginary.valueOf(Real.valueOf(this).divide(n.real()));
    }
}