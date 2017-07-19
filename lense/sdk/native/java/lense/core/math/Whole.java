package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.Any;
import lense.core.lang.java.Constructor;

public abstract class Whole extends Number implements Comparable{

	@Constructor
	public static Whole constructor(){
		return Natural.valueOfNative(0);
	}

	public abstract Whole plus (Whole other);
	public abstract Whole minus (Whole other);
	public abstract Whole multiply(Whole other);
	
	public Rational divide(Whole other){
		return Rational.constructor(this.asInteger(), other.asInteger());
	}
	
	public abstract Whole successor();
	public abstract Whole predecessor();
	
	public abstract boolean isZero();
	public abstract boolean isOne();



	@Override
	public boolean equalsTo(Any other) {
		return other instanceof Whole && ((Whole)other).asBigInteger().compareTo(this.asBigInteger()) == 0;
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

	protected abstract BigInteger asBigInteger();
	
	public abstract Natural abs();
	
	protected abstract Integer asInteger();

	
	protected final int compareTo(Whole other){
		return this.asBigInteger().compareTo(other.asBigInteger());
	}
	
	public Complex plus(Imaginary n ){
		return new Complex(Real.valueOf(this), n.value);
	}
	
	public Complex minus(Imaginary n){
		return new Complex(Real.valueOf(this), n.value.symetric());
	}
	public Imaginary multiply(Imaginary n){
		return Imaginary.valueOf(Real.valueOf(this).multiply(n.value));
	}
	public Imaginary divide(Imaginary n){
		return Imaginary.valueOf(Real.valueOf(this).divide(n.value));
	}
}