package lense.core.math;


import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;

public class Decimal64 extends Decimal{

	@Constructor
	public static Decimal64 constructor (){
		return new Decimal64(0d);
	}
	
	
	@Constructor(isImplicit= true)
	public static Decimal64 valueOf(Real other){
		if (other instanceof Decimal64){
			return (Decimal64)other;
		} else {
			return new Decimal64(other.getNativeBig().doubleValue());
		}
	}
	
	@Constructor(isImplicit= true)
	public static Decimal64 valueOf(Whole other){
		return new Decimal64(other.asBigInteger().doubleValue());
	}
	
	private double value;
	
	Decimal64(double value){
		this.value = value;
	}
	
	public lense.core.lang.String asString(){
		return lense.core.lang.String.valueOfNative(Double.toString(value));
	}
	
	@Override @Native
	protected java.math.BigDecimal getNativeBig() {
		return new java.math.BigDecimal(value);
	}
	
	public Int32 compareTo(Real other){
		return super.compareTo(other);
	}
	
	
	@Override
	public boolean equalsTo(Any other) {
		return other instanceof Decimal64 && Double.compare(((Decimal64)other).value ,this.value) == 0;
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(Double.hashCode(value));
	}
	
	@Override
	public boolean isZero() {
		return Double.compare(this.value, 0) == 0;
	}

	@Override
	public boolean isOne() {
		return Double.compare(this.value, 1) == 0;
	}

	@Override
	public Real plus(Real other) {
		if (other instanceof Decimal64){
			return new Decimal64(this.value + ((Decimal64)other).value);
		} else {
			return promoteNext().plus(other);
		}
	}


	@Override
	public Real minus(Real other) {
		if (other instanceof Decimal64){
			return new Decimal64(this.value - ((Decimal64)other).value);
		} else {
			return promoteNext().minus(other);
		}
	}

	@Override
	public Real multiply(Real other) {
		if (other instanceof Decimal64){
			return new Decimal64(this.value * ((Decimal64)other).value);
		} else {
			return promoteNext().multiply(other);
		}
	}
	
	@Override
	protected Real promoteNext() {
		return new BigDecimal(this.getNativeBig());
	}

	@Override
	public Real divide(Real other) {
		if (other instanceof Decimal64){
			return new Decimal64(this.value / ((Decimal64)other).value);
		} else {
			return promoteNext().divide(other);
		}
	}

	@Override
	public Real symetric() {
		return new Decimal64(-this.value);
	}

	@Override
	public Integer signum() {
		return new Int32((int)Math.signum(this.value));
	}
}
