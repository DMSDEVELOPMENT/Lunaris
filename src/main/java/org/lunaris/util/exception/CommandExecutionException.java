package org.lunaris.util.exception;

/**
 * Created by RINES on 12.09.17.
 */
public class CommandExecutionException extends IllegalStateException {

    public CommandExecutionException(Exception reason) {
        super("An error occurred whilst executing command", reason);
    }

}
