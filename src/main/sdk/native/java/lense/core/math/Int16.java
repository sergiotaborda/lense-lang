package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.Binary;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;
import lense.core.lang.java.Property;

public class Int16 extends Integer implements Binary{

	@Constructor
	public static Int16 constructor (){
		return new Int16((short)0);
	}
	
	private short value;
	
	private Int16(short value){
		this.value = value;
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


}
