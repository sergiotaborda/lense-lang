package lense.core.math;


import lense.core.lang.Any;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;

public class Decimal32 extends Decimal{

	@Constructor
	public static Decimal32 constructor (){
		return new Decimal32(0f);
	}
	
	@Constructor(isImplicit= true)
	public static Decimal32 valueOf(Real other){
		if (other instanceof Decimal32){
			return (Decimal32)other;
		} else {
			return new Decimal32(other.promoteToBigDecimal().value.floatValue());
		}
	}
	
	@Constructor(isImplicit= true)
	public static Decimal32 valueOf(Whole other){
		return new Decimal32(other.asBigInteger().floatValue());
	}
	
	private float value;
	
	private Decimal32(float value){
		this.value = value;
	}
	
	public lense.core.lang.String asString(){
		return lense.core.lang.String.valueOfNative(Float.toString(value));
	}
	
	@Override 
	protected BigDecimal promoteToBigDecimal() {   
		return new BigDecimal(java.math.BigDecimal.valueOf(value));
	}
	
	public Int32 compareTo(Real other){
		return super.compareTo(other);
	}
	
	@Override
	public boolean equalsTo(Any other) {
		return other instanceof Decimal32 && Float.compare(((Decimal32)other).value ,this.value) == 0;
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(Float.hashCode(value));
	}

	@Override
	public Real plus(Real other) {
		if (other instanceof Decimal32){
			return new Decimal32(this.value + ((Decimal32)other).value);
		} else {
			return promoteNext().plus(other);
		}
	}


	@Override
	public Real minus(Real other) {
		if (other instanceof Decimal32){
			return new Decimal32(this.value - ((Decimal32)other).value);
		} else {
			return promoteNext().minus(other);
		}
	}

	@Override
	public Real multiply(Real other) {
		if (other instanceof Decimal32){
			return new Decimal32(this.value * ((Decimal32)other).value);
		} else {
			return promoteNext().multiply(other);
		}
	}

	@Override
	public Real divide(Real other) {
		if (other instanceof Decimal32){
			return new Decimal32(this.value * ((Decimal32)other).value);
		} else {
			return promoteNext().divide(other);
		}
	}
	
	@Override
	public boolean isZero() {
		return Float.compare(this.value, 0) == 0;
	}

	@Override
	public boolean isOne() {
		return Float.compare(this.value, 1) == 0;
	}

	@Override
	protected Real promoteNext() {
		return new Decimal64(value);
	}
	
	@Override
	public Real symetric() {
		return new Decimal32(-this.value);
	}
	
	@Override
	public Integer signum() {
		return new Int32((int)Math.signum(this.value));
	}
}
