package lense.core.lang.java;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a type as being only a value with no identity
 *
 */
@Retention(RetentionPolicy.CLASS)
@ValueClass
public @interface ValueClass {

}
