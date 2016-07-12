package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.Any;
import lense.core.lang.Binary;
import lense.core.lang.Boolean;
import lense.core.lang.TextRepresentable;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;
import lense.core.lang.java.Property;

public class Int16 extends Integer implements Binary, TextRepresentable{

	@Constructor
	public static Int16 constructor (){
		return new Int16((short)0);
	}
	
	public static Int16 valueOfNative (int value){
		return new Int16((short)value);
	}
	
	private short value;
	
	private Int16(short value){
		this.value = value;
	}
	
	@Override
	public String toString() {
		return java.lang.Short.toString(value);
	}
	
	@Override @Native
	protected BigInteger getNativeBig() {
		return BigInteger.valueOf(value);
	}

	@Override @Property(name="size")
	public Natural getSize() {
		return Natural.valueOfNative(16);
	}

	@Override
	public Binary flipAll() {
		return new Int16( (short)(~value) );
	}

	@Override
	public Binary rightShiftBy(Natural n) {
		return new Int16( (short)(value >> n.toPrimitiveInt()) );
	}

	@Override
	public Binary leftShiftBy(Natural n) {
		return new Int16( (short)(value << n.toPrimitiveInt()) );
	}

	public Int16 minus(Int16 other) {
		int result = this.value - other.value;
		if (result >> 15 != 0){
			throw new ArithmeticException();
		}
		return new Int16( (short)(result));
	}
	
	public Int16 plus (Int16 n){
		int result = this.value + n.value;
		if (result >> 15 != 0){
			throw new ArithmeticException();
		}
		return new Int16( (short)(result));
	}

	public Int16 multiply (Int16 n){
		int result = this.value * n.value;
		if (result >> 15 != 0){
			throw new ArithmeticException();
		}
		return new Int16( (short)(result));
	}
	
	public Rational divide (Int16 n){
		return Rational.constructor(this, n);
	}
	
	@Override
	public Boolean equalsTo(Any other) {
		return Boolean.valueOfNative(other instanceof Int16 && ((Int16)other).value == this.value);
	}

	@Override
	public Integer hashValue() {
		return this;
	}

	@Override
	public int hashCode() {
		return value;
	}

	@Override
	public Integer plus(Integer n) {
		if (n instanceof Int16){
			return this.minus((Int16)n);
		} else {
			return BigInt.valueOf(this.getNativeBig().subtract(n.getNativeBig()));
		}
	}

	@Override
	public Integer minus(Integer n) {
		if (n instanceof Int16){
			return this.minus((Int16)n);
		} else {
			return BigInt.valueOf(this.getNativeBig().subtract(n.getNativeBig()));
		}
	}

	@Override
	public Integer multiply(Integer n) {
		if (n instanceof Int16){
			return this.multiply((Int16)n);
		} else {
			return BigInt.valueOf(this.getNativeBig().multiply(n.getNativeBig()));
		}
	}

	@Override
	public Whole plus(Whole n) {
		if (n instanceof Int16){
			return this.plus((Int16)n);
		} else  {
			return BigInt.valueOf(this.getNativeBig()).plus(n);
		} 
	}

	@Override
	public Whole minus(Whole n) {
		if (n instanceof Int16){
			return this.minus((Int16)n);
		} else {
			return BigInt.valueOf(this.getNativeBig()).minus(n);
		} 
	}

	@Override
	public Whole multiply(Whole n) {
		if (n instanceof Int16){
			return this.multiply((Int16)n);
		} else {
			return BigInt.valueOf(this.getNativeBig()).multiply(n);
		}
	}
}
