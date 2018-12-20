package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.reflection.Type;

public interface Number extends Any{

	public boolean isZero();
	
	@Override
	public default Type type() {
		return new Type(this.getClass());
	}
	
	public default lense.core.lang.String asString() {
		return  lense.core.lang.String .valueOfNative(this.toString());
	}
}
