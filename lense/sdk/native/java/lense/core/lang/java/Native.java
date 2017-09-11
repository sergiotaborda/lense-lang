package lense.core.lang.java;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a type or member as a native implementation of a declared type or member in a module or type.
 *
 */
@Retention(RetentionPolicy.CLASS)
@PlatformSpecific
public @interface Native {

	boolean overridable() default false;

}
