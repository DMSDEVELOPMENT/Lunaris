package org.lunaris;

import jline.console.ConsoleReader;
import org.fusesource.jansi.AnsiConsole;
import org.lunaris.util.logger.FormatLogger;
import org.lunaris.util.logger.LoggingOutputStream;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;

/**
 * Created by RINES on 12.09.17.
 */
public class Bootstrap {

    public static void main(String[] args) {
        initializeLogger();
    }

    private static void initializeLogger() {
        System.setProperty("library.jansi.version", "Lunaris");
        AnsiConsole.systemInstall();
        ConsoleReader consoleReader;
        FormatLogger logger;
        try {
            consoleReader = new ConsoleReader();
            consoleReader.setExpandEvents(false);
            logger = new FormatLogger(consoleReader);
            System.setErr(new PrintStream(new LoggingOutputStream(logger, Level.SEVERE), true));
            System.setOut(new PrintStream(new LoggingOutputStream(logger, Level.INFO), true));
        } catch (IOException ex) {
            throw new IllegalStateException("Could not load console worker!");
        }
        new Lunaris(logger, consoleReader);
    }

}
