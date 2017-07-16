package lense.core.math;

import java.math.BigInteger;

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
	
	@Constructor(isImplicit = true)
	public static Int64 valueOf (Binary binary){
		return new Int64(0);
	}
	
	@Native
	public static Int64 valueOfNative(long n){
		return new Int64(n);
	}  
	
	@Native
	public static Int64 valueOfNative(int n){
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

    long value;

	public Int64(long n) {
		this.value = n;
	}
	
	/**
	 *  Int32 + Int32 = Int32 or ArithmeticException
	 *  ScalableInt32 + Int32 =  ScalableInt32 or bigger
	 *  Int32 + ScalableInt32 = ScalableInt32 or bigger;
	 *  ScalableInt32 + ScalableInt32 = ScalableInt32 or bigger
	 */
	@Override
	public Integer plus(Integer other) {
		if (other instanceof Int32){
			return plus(new Int64(((Int32)other).value));
		} else 	if (other instanceof Int64){
			return plus( (Int64)other);
		} else {
			return promoteNext().plus(other);
		}
	}
	
	public Int64 plus(Int64 other) {
		return new Int64(Math.addExact(this.value , other.value));
	}
	
	@Override
	public Integer minus(Integer other) {
		if (other instanceof Int32){
			return minus(new Int64(((Int32)other).value));
		} else 	if (other instanceof Int64){
			return minus( (Int64)other);
		} else {
			return promoteNext().plus(other);
		}
	}
	
	public Int64 minus(Int64 other) {
		return new Int64(Math.subtractExact(this.value , other.value));
	}
	
	@Override
	public Integer multiply(Integer other) {
		if (other instanceof Int32){
			return multiply(new Int64(((Int32)other).value));
		} else 	if (other instanceof Int64){
			return multiply( (Int64)other);
		} else {
			return promoteNext().multiply(other);
		}
	}
	
	public Int64 multiply(Int64 other) {
		return new Int64(Math.multiplyExact(this.value ,other.value));
	}
	
	protected final Integer promoteNext(){
		return new BigInt(BigInteger.valueOf(value));
	}

	@Override
	protected BigInteger asBigInteger() {
		return BigInteger.valueOf(value);
	}

	
	public int compareTo(Integer other ){
		if (other instanceof Int64){
			return Long.compare(this.value,((Int64)other).value );
		} else {
			return asBigInteger().compareTo(other.asBigInteger());
		}
	}


	public final int hashCode(){
		return Long.hashCode(value);
	}
	
	public lense.core.lang.String asString(){
		return lense.core.lang.String.valueOfNative(java.lang.Long.toString(value)); 
	}

	@Override
	public final Integer successor() {
		if (value == java.lang.Integer.MAX_VALUE){
			throw new ArithmeticException();
		}
		return valueOfNative(value + 1);
	}

	@Override
	public boolean isZero() {
		return value == 0;
	}

	@Override
	public boolean isOne() {
		return value == 1;
	}

	@Override
	public final Integer predecessor() {
		if (value == java.lang.Long.MIN_VALUE){
			throw new ArithmeticException();
		}
		return valueOfNative(value - 1);
	}
	
	@Override
	public Natural abs() {
		if (this.value < 0){
			return Natural.valueOfNative(-this.value);
		} else {
			return Natural.valueOfNative(this.value);
		}
	}

	@Override
	@Property(name="size")
	public final Natural getSize() {
		return Natural.valueOfNative(64);
	}

	@Override
	public Int64 flipAll() {
		return new Int64(~value);
	}

	@Override
	public Int64 rightShiftBy(Natural n) {
		return new Int64(value << n.modulus(64));
	}

	@Override
	public Int64 leftShiftBy(Natural n) {
		return new Int64(value >> n.modulus(64));
	}

	@Override
	public Integer symmetric() {
		return new Int64(-value);
	}
	
	public boolean isOdd(){
		return (value & 1) != 0;
	}
	
	public boolean isEven(){
		return (value & 1) == 0;
	}

	@Override
	public boolean getBitAt(Natural index) {
		return rightShiftBy(index).isOdd();
	}
	
	@Override
	public Integer signum() {
		return new Int32( value == 0 ? 0 : (value < 0 ? -1 : 1));
	}
}
