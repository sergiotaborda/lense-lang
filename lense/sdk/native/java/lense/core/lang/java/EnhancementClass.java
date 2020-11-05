package lense.core.lang.java;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a type as being a source of enhancements 
 *
 */
@Retention(RetentionPolicy.CLASS)
@EnhancementClass
public @interface EnhancementClass {

}
