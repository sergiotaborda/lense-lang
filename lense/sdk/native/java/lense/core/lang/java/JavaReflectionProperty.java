package lense.core.lang.java;

import java.lang.reflect.Method;

import lense.core.lang.String;
import lense.core.lang.reflection.Property;

public class JavaReflectionProperty extends Base implements Property {

	private Method javaMethod;

	public JavaReflectionProperty(Method javaMethod, lense.core.lang.java.Property prop) {
		this.javaMethod = javaMethod;
	}

	@Override
	public String getName() {
		return NativeString.valueOfNative(javaMethod.getName());
	}


}
