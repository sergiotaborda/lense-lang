package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.Binary;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;
import lense.core.lang.java.Property;

public class Int64 extends Integer implements Binary{

	@Constructor
	public static Int64 constructor (){
		return new Int64(0);
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

	private long value;  

	private Int64(long value){
		this.value = value;
	}

	public Int64 minus(Int64 other) {
		return new Int64(this.value - other.value);
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
}
