package lense.core.math;

import java.math.BigInteger;

import lense.core.collections.NativeNaturalProgression;
import lense.core.collections.Progression;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;

public abstract class Natural extends Whole  {

	public static final Natural ONE = Natural.valueOfNative(1);
	public static final Natural ZERO = Natural.valueOfNative(0);

	@Constructor
	public static Natural constructor(){
		return Natural.valueOfNative(0);
	}

	@Native
	public static Natural valueOfNative(int value){
		if (value < 0){
			throw new ArithmeticException();
		}
		return new UNat(value);
	}

	@Native
	public static Natural valueOfNative(long value) {
		if (value < 0){
			throw new ArithmeticException();
		}
		return new UNat(value);
	}
	
	public static Natural valueOf(String n) {
		return valueOf(new BigInteger(n));
	}

	public static Natural valueOf(BigInteger n) {
		if (n.signum() < 0){
			throw new ArithmeticException();
		}
		if (n.compareTo(new BigInteger("18446744073709551615")) <= 0){
			return new UNat(n.toString());
		} 
		return new NatBig(n);
	}

	public Progression upTo(Natural other){
		return new NativeNaturalProgression(this, other);
	}

	@Native
	public abstract int toPrimitiveInt();

	public abstract int modulus(int n);

	public abstract Natural plus (Natural other);

	@Override
	public final Whole minus(Whole other) {
		return this.asInteger().minus(other);
	}

	public final Integer minus(Natural other) {
		return this.asInteger().minus(other.asInteger());
	}
	
	public final Integer symmetric() {
		return asInteger().symmetric();
	}
	
	public final boolean isLessThen(Natural other) {
		return  compareTo(other) < 0;
	}
	
	public final boolean isLessOrEqualTo(Natural other) {
		return  compareTo(other) <= 0;
	}

	public abstract Natural successor();
	public abstract Natural predecessor();

	public abstract boolean isZero();

	public abstract boolean isOne();

	

	public abstract Natural multiply(Natural other);

	@Override
	public Whole plus(Whole other) {
		if (other instanceof Natural){
			return this.plus((Natural)other);
		} else {
			return this.asInteger().plus(other.asInteger());
		}
	}

	@Override
	public Whole multiply(Whole other) {
		if (other instanceof Natural){
			return this.multiply((Natural)other);
		} else {
			return this.asInteger().multiply(other.asInteger());
		}
	}
	
	public Integer multiply(Integer other) {
		return this.asInteger().multiply(other.asInteger());
	}

	@Override
	public final Natural abs() {
		return this;
	}
}

