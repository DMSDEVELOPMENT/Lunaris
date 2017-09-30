package org.lunaris.util.logger;

import jline.console.ConsoleReader;

import org.lunaris.Lunaris;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author RinesThaix
 */
public class OwnLogger extends Logger {

    private final Formatter formatter = new ConciseFormatter();
    private final LogDispatcher dispatcher = new LogDispatcher(this);

    @SuppressWarnings({"CallToPrintStackTrace", "CallToThreadStartDuringObjectConstruction"})
    public OwnLogger(ConsoleReader consoleReader) {
        super(null, null);
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

    public static Logger getLogger(String name) {
        Logger logger = new Logger(name, null) {
            OwnLogger ownLogger = Lunaris.getInstance().getLogger();

            @Override
            public void log(LogRecord record) {
                ownLogger.log(record);
            }
        };
        logger.setUseParentHandlers(false);
        return logger;
    }
}
