package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.java.Constructor;

public abstract class Decimal extends Real {

	@Constructor(paramsSignature = "")
	public static Decimal constructor (){
		return Decimal32.constructor();
	}
	
	protected abstract Real promoteNext();
	
	@Override
	public boolean equalsTo(Any other) {
		if (other instanceof Real) {
			this.promoteToBigDecimal().compareWith((Real)other);
		} else if (other instanceof Whole) {
			this.promoteToBigDecimal().compareWith(Real.valueOf((Whole)other));
		}
		return false;
	}

	public Rational asRational() {
		return promoteToBigDecimal().asRational();
	}
}
