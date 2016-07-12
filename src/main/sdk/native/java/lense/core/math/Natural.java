package lense.core.math;

import java.math.BigInteger;

import lense.core.collections.NativeBigIntegerProgression;
import lense.core.collections.Progression;
import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;

public class Natural extends Whole {

	@Constructor
	public static Natural constructor(){
		return Natural.valueOfNative(0);
	}
	
	@Native
	public static Natural valueOfNative(int value){
		return new Natural(value);
	}

	private BigInteger value; 
	
	private Natural(int value){
		this.value = BigInteger.valueOf(value);
	}
	
	public static Natural valueOf(BigInteger i) {
		return new Natural(i);
	}
	
	private Natural(BigInteger value){
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}

	
	@Native
	public int toPrimitiveInt() {
		return value.intValue();
	}

	
	public Integer minus (Natural n){
		return Int32.valueOf(this).minus(Int32.valueOf(n));
	}
	
	public Natural plus (Natural n){
		return new Natural(this.value.add(n.value));
	}

	public Natural multiply (Natural n){
		return new Natural(this.value.multiply(n.value));
	}
	
	public Rational divide (Natural n){
		return new Rational(this.value, n.value);
	}
	
	public Progression upTo(Natural other){
		return new NativeBigIntegerProgression(value, other.value, BigInteger.ONE);
	}
	

	@Override
	public Boolean equalsTo(Any other) {
		return Boolean.valueOfNative(other instanceof Natural && ((Natural)other).value.compareTo(this.value) == 0);
	}

	@Override
	public Integer hashValue() {
		return BigInt.valueOf(value);
	}

	@Override
	public Whole plus(Whole n) {
		if (n instanceof Integer){
			return BigInt.valueOf(this.getNativeBig()).plus(n);
		} else {
			return this.plus((Natural)n);
		}
	}

	@Override
	public Whole minus(Whole n) {
		if (n instanceof Integer){
			return BigInt.valueOf(this.getNativeBig()).minus(n);
		} else {
			return this.minus((Natural)n);
		}
	}

	@Override
	public Whole multiply(Whole n) {
		if (n instanceof Integer){
			return BigInt.valueOf(this.getNativeBig()).multiply((Integer)n);
		} else {
			return this.multiply((Natural)n);
		}
	}

	BigInteger getNativeBig() {
		return value;
	}

	@Override
	public Comparison compareTo(Any other) {
		int comp = this.value.compareTo(((Whole)other).getNativeBig());
		if (comp > 0){
			return Greater.constructor();
		} else if (comp < 0){
			return Smaller.constructor();
		} else{
			return Equal.constructor();
		}
	}


}
