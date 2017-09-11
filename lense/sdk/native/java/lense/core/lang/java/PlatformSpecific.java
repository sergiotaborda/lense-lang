package lense.core.lang.java;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a type or member as platform implementation detail.
 *
 */
@Retention(RetentionPolicy.CLASS)
@PlatformSpecific
public @interface PlatformSpecific {

}
