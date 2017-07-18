package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.java.Constructor;

public class BigInt extends Integer {

    private BigInteger value;

    @Constructor
    public static BigInt constructor(){
    	return new BigInt(BigInteger.ZERO);
    }
    
	BigInt(BigInteger n) {
		this.value = n;
	}

	@Override
	public Integer plus(Integer other) {
		return new BigInt(this.value.add(other.asBigInteger()));
	}
	
	@Override
	public Integer minus(Integer other) {
		return new BigInt(this.value.subtract(other.asBigInteger()));
	}
			
	@Override
	protected BigInteger asBigInteger() {
		return value;
	}

	@Override
	public Integer successor() {
		return new BigInt(this.value.add(BigInteger.ONE));
	}

	@Override
	public boolean isZero() {
		return value.signum() == 0;
	}

	@Override
	public boolean isOne() {
		return value.compareTo(BigInteger.ONE) == 0;
	}

	@Override
	public Integer predecessor() {
		return new BigInt(this.value.subtract(BigInteger.ONE));
	}

	@Override
	public Integer multiply(Integer predecessor) {
		return new BigInt(this.value.multiply(BigInteger.ONE));
	}

	@Override
	public Natural abs() {
		return new NatBig(this.value.abs());
	}
	
	public lense.core.lang.String asString(){
		return lense.core.lang.String.valueOfNative(value.toString());
	}

	@Override
	public Integer symmetric() {
		return new BigInt(this.value.negate());
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public Integer signum() {
		return new Int32(value.signum());
	}



	



}
