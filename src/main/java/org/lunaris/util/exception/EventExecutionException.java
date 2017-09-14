package org.lunaris.util.exception;

/**
 * Created by RINES on 13.09.17.
 */
public class EventExecutionException extends IllegalStateException {

    public EventExecutionException(Exception reason) {
        super("An error occurred with event handling", reason);
    }

}
