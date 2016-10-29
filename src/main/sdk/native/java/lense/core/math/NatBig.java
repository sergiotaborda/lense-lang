package lense.core.math;

import java.math.BigInteger;

public class NatBig extends Natural{

	private BigInteger value;

	public NatBig(long value) {
		this.value = BigInteger.valueOf(value);
	}

	NatBig(BigInteger value) {
		this.value = value;
	}
	
	@Override
	public Natural plus(Natural other) {
		return new NatBig(this.value.add(other.asBigInteger()));
	}

	@Override
	public Natural multiply(Natural other) {
		return new NatBig(this.value.multiply(other.asBigInteger()));
	}
	
	protected BigInteger asBigInteger(){
		return value;
	}

	public final Integer hashValue(){
		return Int32.valueOfNative(value.hashCode());
	}
	
	public lense.core.lang.String asString(){
		return lense.core.lang.String.valueOfNative(value.toString()); 
	}

	@Override
	public Natural successor() {
		return new NatBig(this.value.add(BigInteger.ONE));
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
	public Natural predecessor() {
		if (value.signum() == 0){
			throw new ArithmeticException();
		}
		return new NatBig(value.subtract(BigInteger.ONE));
	}

	@Override
	protected Integer asInteger() {
		return new BigInt(value);
	}

	@Override
	public int toPrimitiveInt() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int modulus(int n) {
		return this.value.remainder(BigInteger.valueOf(n)).intValueExact();
	}

}
