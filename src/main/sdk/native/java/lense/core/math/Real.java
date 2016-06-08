package lense.core.math;

import java.math.BigDecimal;

public abstract class Real extends Number{

	
	public Int32 compareTo(lense.core.lang.Any other){
		Real r = (Real)other;
		return Int32.valueOfNative(this.getNativeBig().compareTo(r.getNativeBig()));
	}

	protected abstract BigDecimal getNativeBig();
}
