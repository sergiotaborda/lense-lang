package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.Any;
import lense.core.lang.Binary;
import lense.core.lang.Boolean;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;
import lense.core.lang.java.Property;

public class Int32 extends Integer implements Binary {

	@Constructor
	public static Int32 constructor (){
		return new Int32(0);
	}
	
	@Native
	public static Int32 valueOfNative(int n){
		return new Int32(n);
	}  
	
	@Constructor(isImplicit = true)
	public static Int32 valueOf(Natural n){
		// TODO validate overflow
		return new Int32(n.toPrimitiveInt());
	}  
	
	@Constructor
	public static Int32 parse(String s){
		return new Int32(java.lang.Integer.parseInt(s));
	}

	private int value;  

	private Int32(int value){
		this.value = value;
	}

	@Override
	public String toString() {
		return java.lang.Integer.toString(value);
	}
	
	public Int32 minus(Int32 other) {
		return new Int32(Math.subtractExact(this.value , other.value));
	}
	
	public Int32 plus (Int32 other){
		return new Int32(Math.addExact(this.value , other.value));
	}

	public Int32 multiply (Int32 other){
		return new Int32(Math.multiplyExact(this.value , other.value));
	}
	
	public Rational divide (Int32 n){
		return Rational.constructor(this, n);
	}
	
	@Override @Native
	protected BigInteger getNativeBig() {
		return BigInteger.valueOf(value);
	}
	

	@Override
	public Binary flipAll() {
		return new Int32(~value);
	}

	@Override
	public Binary rightShiftBy(Natural n) {
		return new Int32( value >> n.toPrimitiveInt());
	}

	@Override
	public Binary leftShiftBy(Natural n) {
		return new Int32( value << n.toPrimitiveInt());
	}

	@Override @Property(name="size")
	public Natural getSize() {
		return Natural.valueOfNative(32);
	}
	
	@Override
	public Boolean equalsTo(Any other) {
		return Boolean.valueOfNative(other instanceof Int32 && ((Int32)other).value == this.value);
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
		if (n instanceof Int32){
			return this.minus((Int32)n);
		} else {
			return BigInt.valueOf(this.getNativeBig().subtract(n.getNativeBig()));
		}
	}

	@Override
	public Integer minus(Integer n) {
		if (n instanceof Int32){
			return this.minus((Int32)n);
		} else {
			return BigInt.valueOf(this.getNativeBig().subtract(n.getNativeBig()));
		}
	}

	@Override
	public Integer multiply(Integer n) {
		if (n instanceof Int32){
			return this.multiply((Int32)n);
		} else {
			return BigInt.valueOf(this.getNativeBig().multiply(n.getNativeBig()));
		}
	}

	@Override
	public Whole plus(Whole n) {
		if (n instanceof Int32){
			return this.plus((Int32)n);
		} else  {
			return BigInt.valueOf(this.getNativeBig()).plus(n);
		} 
	}

	@Override
	public Whole minus(Whole n) {
		if (n instanceof Int32){
			return this.minus((Int32)n);
		} else {
			return BigInt.valueOf(this.getNativeBig()).minus(n);
		} 
	}

	@Override
	public Whole multiply(Whole n) {
		if (n instanceof Int32){
			return this.multiply((Int32)n);
		} else {
			return BigInt.valueOf(this.getNativeBig()).multiply(n);
		}
	}
}
