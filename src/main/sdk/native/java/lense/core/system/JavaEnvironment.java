package lense.core.system;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.math.Int32;
import lense.core.math.Integer;

class JavaEnvironment implements Environment{

	@Override
	public Boolean equalsTo(Any other) {
		// TODO Auto-generated method stub
		return Boolean.valueOfNative(this == other);
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(0);
	}

}
