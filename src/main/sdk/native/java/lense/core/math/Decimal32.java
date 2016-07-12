package lense.core.math;

import java.math.BigDecimal;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;

public class Decimal32 extends Decimal{

	@Constructor
	public static Decimal32 constructor (){
		return new Decimal32();
	}
	
	private float value = 0f;
	
	private Decimal32(){
		
	}

	@Override @Native
	protected BigDecimal getNativeBig() {
		return new BigDecimal(value);
	}
	
	public Int32 compareTo(Real other){
		return super.compareTo(other);
	}
	
	@Override
	public Boolean equalsTo(Any other) {
		return Boolean.valueOfNative(other instanceof Decimal32 && Float.compare(((Decimal32)other).value ,this.value) == 0);
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(hashCode());
	}
	
	@Override
	public final int hashCode() {
		return Float.hashCode(value);
	}
}
