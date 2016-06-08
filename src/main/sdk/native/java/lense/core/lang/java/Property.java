package lense.core.lang.java;

public @interface Property {

	String name () default "";
	boolean indexed () default false;
	boolean setter() default false;
}
