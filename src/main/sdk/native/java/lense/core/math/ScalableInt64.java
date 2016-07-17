package lense.core.math;

import java.math.BigInteger;

public class ScalableInt64 extends ScalableInteger {

   long value;

	public ScalableInt64(long n) {
		this.value = n;
	}

	public static ScalableInt64 valueOf(int n){
		return new ScalableInt64(n);
	}
	
	@Override
	public Integer plus(Integer other) {
		if (other instanceof ScalableInt64){
			return plus(((ScalableInt64)other).value);
		} else if (other instanceof ScalableInt32){
			return plus(((ScalableInt32)other).value);
		} else if (other instanceof Int32){
			return plus(((Int32)other).value);
		} else if (other instanceof Int64){
			return plus(((Int64)other).value);
		} else {
			return promoteNext().plus(other);
		}
		
	}
	
	private Integer plus(long other) {
		try {
			return new ScalableInt64(Math.addExact(this.value , other));
		}catch (ArithmeticException e ){
			return promoteNext().plus(new ScalableInt64(other));
		}
	}
	
	private Integer minus(long other) {
		try {
			return new ScalableInt64(Math.subtractExact(this.value , other));
		}catch (ArithmeticException e ){
			return promoteNext().plus(new ScalableInt64(other));
		}
	}
	
	@Override
	public Integer minus(Integer other) {
		if (other instanceof ScalableInt64){
			return minus(((ScalableInt64)other).value);
		} else if (other instanceof ScalableInt32){
			return minus(((ScalableInt32)other).value);
		} else if (other instanceof Int32){
			return minus(((Int32)other).value);
		} else if (other instanceof Int64){
			return minus(((Int64)other).value);
		} else {
			return promoteNext().minus(other);
		}
	}
	
	
	@Override
	public Integer multiply(Integer other) {
		try {
			return new ScalableInt64(Math.multiplyExact(this.value , ((ScalableInt64)other).value));
		} catch (ClassCastException e ){
			return promoteNext().plus(other);
		}catch (ArithmeticException e ){
			return promoteNext().plus(other);
		}
	}
	
	protected final Integer promoteNext(){
		return new BigInt(BigInteger.valueOf(value));
	}

	@Override
	protected BigInteger asBigInteger() {
		return BigInteger.valueOf(value);
	}

	
	public int compareTo(Integer other ){
		if (other instanceof ScalableInt64){
			return Long.compare(this.value,((ScalableInt64)other).value );
		} else {
			return asBigInteger().compareTo(other.asBigInteger());
		}
	}

	public final int hashCode(){
		return Long.hashCode(value);
	}
	
	public final String toString(){
		return java.lang.Long.toString(value); 
	}

	@Override
	public final Integer successor() {
		if (value == java.lang.Integer.MAX_VALUE){
			return (Integer)promoteNext().successor();
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
			return (Integer)this.promoteNext().predecessor();
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
	public Integer symmetric() {
		return new ScalableInt64(-value);
	}



}
