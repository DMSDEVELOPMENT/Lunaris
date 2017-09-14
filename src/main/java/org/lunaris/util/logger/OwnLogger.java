package org.lunaris.util.logger;

import jline.console.ConsoleReader;

import java.io.IOException;
import java.util.logging.*;

/**
 * @author RinesThaix
 */
public class OwnLogger extends Logger {

    private final Formatter formatter = new ConciseFormatter();
    private final LogDispatcher dispatcher = new LogDispatcher(this);

    @SuppressWarnings({"CallToPrintStackTrace", "CallToThreadStartDuringObjectConstruction"})
    public OwnLogger(ConsoleReader consoleReader) {
        super("Lunaris", null);
        setLevel(Level.ALL);
        try {
            FileHandler fileHandler = new FileHandler("lunaris.log", 1 << 24, 8, true);
            fileHandler.setFormatter(formatter);
            addHandler(fileHandler);

            ColouredWriter consoleHandler = new ColouredWriter(consoleReader);
            consoleHandler.setLevel(Level.INFO);
            consoleHandler.setFormatter(formatter);
            addHandler(consoleHandler);
        } catch (IOException ex) {
            System.err.println("Could not register logger!");
            ex.printStackTrace();
        }
        dispatcher.start();
    }

    @Override
    public void log(LogRecord record) {
        dispatcher.queue(record);
    }

    void doLog(LogRecord record) {
        super.log(record);
    }
}
