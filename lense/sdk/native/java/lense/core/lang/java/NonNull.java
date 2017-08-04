package lense.core.lang.java;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@Native
public @interface NonNull {

}
