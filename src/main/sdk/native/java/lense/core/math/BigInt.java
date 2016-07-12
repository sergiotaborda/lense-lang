package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;

public class BigInt extends Integer{

	@Constructor
	public static BigInt constructor (){
		return new BigInt();
	}
	
	private java.math.BigInteger value;
	
	private BigInt(java.math.BigInteger value){
		this.value = value;
	}
	
	private BigInt(){
		this(java.math.BigInteger.ZERO);
	}
	
	@Native
	public static Integer valueOf(BigInteger value) {
		return new BigInt(value);
	}
	
	@Override @Native
	protected BigInteger getNativeBig() {
		return value;
	}

	@Override
	public Boolean equalsTo(Any other) {
		return Boolean.valueOfNative(other instanceof BigInt && ((BigInt)other).value.compareTo(this.value) == 0);
	}

	@Override
	public Integer hashValue() {
		return this;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public Integer plus(Integer n) {
		 return new BigInt(this.value.add(n.getNativeBig()));
	}

	@Override
	public Integer minus(Integer n) {
		 return new BigInt(this.value.subtract(n.getNativeBig()));
	}

	@Override
	public Integer multiply(Integer n) {
		 return new BigInt(this.value.multiply(n.getNativeBig()));
	}

	@Override
	public Whole plus(Whole n) {
		return new BigInt(this.value.add(n.getNativeBig()));
		
	}

	@Override
	public Whole minus(Whole n) {
		return new BigInt(this.value.subtract(n.getNativeBig()));
	}

	@Override
	public Whole multiply(Whole n) {
		return new BigInt(this.value.multiply(n.getNativeBig()));
	}

	
}
