package lense.core.system;

import lense.core.lang.Any;
import lense.core.lang.Boolean;
import lense.core.lang.String;
import lense.core.math.Int32;
import lense.core.math.Integer;

class JavaEnvironment implements Environment{

	@Override
	public boolean equalsTo(Any other) {
		return this == other;
	}

	@Override
	public Integer hashValue() {
		return Int32.valueOfNative(0);
	}

	@Override
	public String asString() {
		return String.valueOfNative("JavaEnvironment");
	}

}
