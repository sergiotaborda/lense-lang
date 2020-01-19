package lense.core.lang.java;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a type for replacement by a pure language implementation
 */
@Retention(RetentionPolicy.CLASS)
@Placeholder
public @interface Placeholder {

}
