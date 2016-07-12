package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.Any;
import lense.core.lang.Binary;
import lense.core.lang.Boolean;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;
import lense.core.lang.java.Property;

public class Int64 extends Integer implements Binary{

	@Constructor
	public static Int64 constructor (){
		return new Int64(0);
	}
	
	@Native
	public static Int64 valueOfNative(long n){
		return new Int64(n);
	}  
	
	@Constructor
	public static Int64 valueOf(Natural n){
		// TODO validate overflow
		return new Int64(n.toPrimitiveInt());
	}  
	
	@Constructor
	public static Int64 parse(String s){
		return new Int64(java.lang.Long.parseLong(s));
	}

	private long value;  

	private Int64(long value){
		this.value = value;
	}

	@Override
	public String toString() {
		return Long.toString(value);
	}
	
	public Int64 minus(Int64 other) {
		return new Int64(Math.subtractExact(this.value , other.value));
	}
	
	public Int64 plus (Int64 other){
		return new Int64(Math.addExact(this.value , other.value));
	}

	public Int64 multiply (Int64 other){
		return new Int64(Math.multiplyExact(this.value , other.value));
	}
	
	public Rational divide (Int64 n){
		return Rational.constructor(this, n);
	}
	
	@Override @Native
	protected BigInteger getNativeBig() {
		return BigInteger.valueOf(value);
	}
	
	@Override
	public Binary flipAll() {
		return new Int64(~value);
	}

	@Override
	public Binary rightShiftBy(Natural n) {
		return new Int64( value >> n.toPrimitiveInt());
	}

	@Override
	public Binary leftShiftBy(Natural n) {
		return new Int64( value << n.toPrimitiveInt());
	}

	@Override @Property(name="size")
	public Natural getSize() {
		return Natural.valueOfNative(64);
	}
	
	@Override
	public Boolean equalsTo(Any other) {
		return Boolean.valueOfNative(other instanceof Int64 && ((Int64)other).value == this.value);
	}

	@Override
	public Integer hashValue() {
		return this;
	}
	@Override
	public final int hashCode() {
		return Long.hashCode(value);
	}
	@Override
	public Integer plus(Integer n) {
		if (n instanceof Int64){
			return this.minus((Int64)n);
		} else {
			return BigInt.valueOf(this.getNativeBig().subtract(n.getNativeBig()));
		}
	}

	@Override
	public Integer minus(Integer n) {
		if (n instanceof Int64){
			return this.minus((Int64)n);
		} else {
			return BigInt.valueOf(this.getNativeBig().subtract(n.getNativeBig()));
		}
	}

	@Override
	public Integer multiply(Integer n) {
		if (n instanceof Int64){
			return this.multiply((Int64)n);
		} else {
			return BigInt.valueOf(this.getNativeBig().multiply(n.getNativeBig()));
		}
	}

	@Override
	public Whole plus(Whole n) {
		if (n instanceof Int64){
			return this.plus((Int64)n);
		} else  {
			return BigInt.valueOf(this.getNativeBig()).plus(n);
		} 
	}

	@Override
	public Whole minus(Whole n) {
		if (n instanceof Int64){
			return this.minus((Int64)n);
		} else {
			return BigInt.valueOf(this.getNativeBig()).minus(n);
		} 
	}

	@Override
	public Whole multiply(Whole n) {
		if (n instanceof Int64){
			return this.multiply((Int64)n);
		} else {
			return BigInt.valueOf(this.getNativeBig()).multiply(n);
		}
	}
}
