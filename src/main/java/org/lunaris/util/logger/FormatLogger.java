package org.lunaris.util.logger;

import jline.console.ConsoleReader;

/**
 * Created by RINES on 12.09.17.
 */
public class FormatLogger extends OwnLogger {

    public FormatLogger(ConsoleReader consoleReader) {
        super(consoleReader);
    }

    public void info(String s, Object... args) {
        super.info(String.format(s, args));
    }

    public void severe(String s, Object... args) {
        super.severe(String.format(s, args));
    }

    public void warn(String s, Object... args) {
        super.warning(String.format(s, args));
    }

    public void warn(Throwable t, String s) {
        super.warning(s);
        t.printStackTrace();
    }

    public void warn(Throwable t, String s, Object... args) {
        warn(t, String.format(s, args));
    }

    public void error(String s) {
        super.severe(s);
    }

    public void error(String s, Object... args) {
        super.severe(String.format(s, args));
    }

    public void error(Throwable t, String s) {
        t.printStackTrace();
        error(s);
    }

    public void error(Throwable t, String s, Object... args) {
        error(t, String.format(s, args));
    }

}
