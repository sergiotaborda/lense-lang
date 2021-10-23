package lense.core.lang.reflection;

import lense.core.lang.String;
import lense.core.lang.java.Base;

public class JavaReflectionMethod extends Base implements Method {

	private java.lang.reflect.Method javaMethod;

	public JavaReflectionMethod(java.lang.reflect.Method javaMethod) {
		this.javaMethod = javaMethod;
	}

	@Override
	public String getName() {
		return String.valueOfNative(javaMethod.getName());
	}

}
