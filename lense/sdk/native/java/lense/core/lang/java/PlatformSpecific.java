package lense.core.lang.java;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a type or member as platform implementation detail.
 * Theses types are not visible to program code, just to compilations.
 */
@Retention(RetentionPolicy.CLASS)
@PlatformSpecific
public @interface PlatformSpecific {

}
