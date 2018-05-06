package lense.core.lang.java;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@PlatformSpecific
public @interface Signature {

	String value(); //variable_name:super_class_type_bound:interface_type_bounds
	String caseValues() default "";
	String caseTypes() default "";
}
