package lense.core.lang.java;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
@Native
public @interface Constructor {

	boolean isImplicit() default false ;
}