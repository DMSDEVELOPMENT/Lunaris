package org.lunaris.util.exception;

/**
 * Created by RINES on 12.09.17.
 */
public class TaskInvocationException extends IllegalStateException {

    public TaskInvocationException(Exception reason) {
        super("An error occurred whilst executing scheduled task", reason);
    }

}
