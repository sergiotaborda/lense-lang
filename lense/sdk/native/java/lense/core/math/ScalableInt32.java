package lense.core.math;

import java.math.BigInteger;

public class ScalableInt32 extends ScalableInteger {

   int value;

	private ScalableInt32(int n) {
		this.value = n;
	}

	public static ScalableInt32 valueOf(int n){
		return new ScalableInt32(n);
	}
	
	@Override
	public Integer plus(Integer other) {
		try {
			return new ScalableInt32(Math.addExact(this.value , ((ScalableInt32)other).value));
		} catch (ClassCastException e ){
			return promoteNext().plus(other);
		}catch (ArithmeticException e ){
			return promoteNext().plus(other);
		}
	}
	
	@Override
	public Integer minus(Integer other) {
		try {
			return new ScalableInt32(Math.subtractExact(this.value , ((ScalableInt32)other).value));
		} catch (ClassCastException e ){
			return promoteNext().minus(other);
		}catch (ArithmeticException e ){
			return promoteNext().minus(other);
		}
	}
	
	@Override
	public Integer multiply(Integer other) {
		try {
			return new ScalableInt32(Math.multiplyExact(this.value , ((ScalableInt32)other).value));
		} catch (ClassCastException e ){
			return promoteNext().multiply(other);
		}catch (ArithmeticException e ){
			return promoteNext().multiply(other);
		}
	}
	
	protected final Integer  promoteNext(){
		return ScalableInt64.valueOf(value);
	}

	@Override
	protected BigInteger asBigInteger() {
		return BigInteger.valueOf(value);
	}

	public int compareTo(Integer other ){
		if (other instanceof ScalableInt32){
			return  java.lang.Integer.compare(this.value, ((ScalableInt32)other).value);
		} else {
			return asBigInteger().compareTo(other.asBigInteger());
		}
	}

	public final int hashCode(){
		return this.value;
	}
	
	public final lense.core.lang.String asString(){
		return lense.core.lang.String.valueOfNative(java.lang.Integer.toString(value)); 
	}

	@Override
	public final Integer successor() {
		if (value == java.lang.Integer.MAX_VALUE){
			return (Integer)this.promoteNext().successor();
		}
		return valueOf(value + 1);
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
		if (value == java.lang.Integer.MIN_VALUE){
			return (Integer)this.promoteNext().predecessor();
		}
		return valueOf(value - 1);
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
		return new ScalableInt32(-value);
	}

	@Override
	public Integer signum() {
		return new Int32( value == 0 ? 0 : (value < 0 ? -1 : 1));
	}



}
