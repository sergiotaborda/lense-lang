package lense.core.math;

import java.math.BigDecimal;

public class Rational extends Real{

	@Override
	protected BigDecimal getNativeBig() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Int32 compareTo(lense.core.lang.Any other){
		Real r = (Real)other;
		return Int32.valueOfNative(this.getNativeBig().compareTo(r.getNativeBig()));
	}

	
	
}
