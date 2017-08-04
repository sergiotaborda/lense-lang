package lense.core.system;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.String;
import lense.core.lang.java.Base;

class JavaEnvironment  extends Base implements Environment{

	@Override
	public boolean equalsTo(Any other) {
		return this == other;
	}

	@Override
	public HashValue hashValue() {
		return new HashValue(0);
	}

	@Override
	public String asString() {
		return String.valueOfNative("JavaEnvironment");
	}

}
