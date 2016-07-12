package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.Any;
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

	public Comparison compareTo(Any other){

		int comp = this.getNativeBig().compareTo(((Whole)other).getNativeBig());
		if (comp > 0){
			return Greater.constructor();
		} else if (comp < 0){
			return Smaller.constructor();
		} else{
			return Equal.constructor();
		}

	}

	@Native
	protected abstract BigInteger getNativeBig();

	public boolean equals(Object other){
		return  other instanceof Any && this.equalsTo((Any)other).toPrimitiveBoolean();
	}

	public abstract  int hashCode ();

	public abstract Integer plus(Integer n);

	public abstract Integer minus(Integer n);

	public abstract Integer multiply(Integer n);

	public Rational divide(Integer other){
		return Rational.constructor(this, other);
	}


}
