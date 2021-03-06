package lense.core.lang.java;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
@PlatformSpecific
public @interface MethodSignature {

	String returnSignature();
	String paramsSignature();
	String declaringType() default "";
	String boundedTypes() default "";
	boolean override () default false;
}
