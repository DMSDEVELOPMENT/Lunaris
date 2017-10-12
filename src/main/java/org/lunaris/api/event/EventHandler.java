package org.lunaris.api.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by RINES on 13.09.17.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface EventHandler {

    boolean ignoreCancelled() default false;

    EventPriority priority() default EventPriority.NORMAL;

}
