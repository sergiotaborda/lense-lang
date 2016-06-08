package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.Binary;
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

	public Int32 minus(Int32 other) {
		return new Int32(this.value - other.value);
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
}
