package lense.core.math;

import java.math.BigInteger;

import lense.core.collections.NativeNaturalProgression;
import lense.core.collections.Progression;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;
import lense.core.lang.java.NonNull;

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
		    throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("A negative integer cannot be transformed to a Natural"));
		}
		return new UNat(value);
	}

	@Native
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
		return new NatBig(n);
	}

	public @NonNull Progression upTo(@NonNull Natural other){
		return new NativeNaturalProgression(this, other);
	}

	@Native
	public abstract int toPrimitiveInt();

	public abstract int modulus(int n);

	public abstract @NonNull Natural plus (@NonNull Natural other);

	@Override
	public final @NonNull Whole minus(@NonNull Whole other) {
		return this.asInteger().minus(other);
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

	

	public abstract @NonNull Natural multiply(@NonNull Natural other);

	@Override
	public @NonNull Whole plus(@NonNull Whole other) {
		if (other instanceof Natural){
			return this.plus((Natural)other);
		} else {
			return this.asInteger().plus(other.asInteger());
		}
	}

	@Override
	public @NonNull Whole multiply(@NonNull Whole other) {
		if (other instanceof Natural){
			return this.multiply((Natural)other);
		} else {
			return this.asInteger().multiply(other.asInteger());
		}
	}
	
	public @NonNull Integer multiply(@NonNull Integer other) {
		return this.asInteger().multiply(other.asInteger());
	}

	@Override
	public final @NonNull Natural abs() {
		return this;
	}
}

