package lense.core.math;

import java.math.BigInteger;

public abstract class Integer extends Whole{

	public Int32 compareTo(Integer other){
		return Int32.valueOfNative(this.getNativeBig().compareTo(other.getNativeBig()));
	}
	
	protected abstract BigInteger getNativeBig();
}
