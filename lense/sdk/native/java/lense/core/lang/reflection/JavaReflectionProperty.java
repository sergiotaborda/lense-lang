package lense.core.lang.reflection;

import java.lang.reflect.Method;

import lense.core.lang.String;
import lense.core.lang.java.Base;

public class JavaReflectionProperty extends Base implements Property {

	private Method javaMethod;

	public JavaReflectionProperty(Method javaMethod, lense.core.lang.java.Property prop) {
		this.javaMethod = javaMethod;
	}

	@Override
	public String getName() {
		return String.valueOfNative(javaMethod.getName());
	}


}
