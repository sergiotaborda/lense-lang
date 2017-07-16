package lense.core.lang.java;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
@Native
public @interface Property {

	String name () default "";
	boolean indexed () default false;
	boolean setter() default false;
}
