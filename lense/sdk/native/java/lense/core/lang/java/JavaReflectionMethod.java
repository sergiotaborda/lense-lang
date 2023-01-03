package lense.core.lang.java;

import lense.core.lang.String;
import lense.core.lang.reflection.Method;

public class JavaReflectionMethod extends Base implements Method {

	private java.lang.reflect.Method javaMethod;

	public JavaReflectionMethod(java.lang.reflect.Method javaMethod) {
		this.javaMethod = javaMethod;
	}

	@Override
	public String getName() {
		return NativeString.valueOfNative(javaMethod.getName());
	}

}
