package lense.core.lang;

import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;
import lense.core.math.Int32;
import lense.core.math.Integer;
import lense.core.math.Natural;
import lense.core.math.Natural64;

public class Byte extends Base implements Binary{

	@Constructor(isImplicit = true, paramsSignature = "lense.core.lang.Binary")
	public static Byte valueOf (Binary n){
		// ignoring other bit is equivalent to a % 256 operation
		int value = 0;
		for (int i =0; i < 8 ; i ++){
			value += Math.pow(2, i) * (n.bitAt(Natural64.valueOfNative(i)) ? 1 :  0);
		}
		return new Byte(value);
	}
	
	private Byte(int value) {
		this.value = value;
	}


	// return between -128 and 127
	public Integer toInteger(){
		return Int32.valueOfNative(value - 128);
	}
	
	// return between 0 and 255
	public Natural toNatural(){
		return Natural64.valueOfNative(value);
	}

	private int value; // positive number 0000_0000_0000_0000_0000_0000_XXXX_XXXX
	
	@Override
	public Natural bitsCount() {
		return Natural64.valueOfNative(8);
	}

	@Override
	public Byte complement() {
		return new Byte(~value & 0x000000FF);
	}

	@Override
	public Byte rightShiftBy(Natural n) {
		return new Byte(value << n.modulus(8));
	}

	@Override
	public Byte leftShiftBy(Natural n) {
		return new Byte(value >> n.modulus(8));
	}

	@Override
	public boolean bitAt(Natural index) {
		return rightShiftBy(index).lowerBit();
	}
	
	private boolean lowerBit(){
		return (value & 1) != 0; 
	}
 
	// TODO create Base to implement all java adapting methods like equals and hashcode 
	
	@Override
	public boolean equalsTo(Any other) {
		 return  other instanceof Byte && ((Byte)other).value == this.value; 
	}

	@Override
	public HashValue hashValue() {
		return new HashValue(value);
	}

	@Override
	public String asString() {
		return String.valueOfNative(java.lang.Integer.toString(value));
	}

    @Override
    public Byte xor(Any other) { // other is Binary
       throw new UnsupportedOperationException("Not implement yet");
    }

    @Override
    public Byte or(Any other) {
    	   throw new UnsupportedOperationException("Not implement yet");
    }

    @Override
    public Byte and(Any other) {
        // TODO Auto-generated method stub
        return null;
    }


}
