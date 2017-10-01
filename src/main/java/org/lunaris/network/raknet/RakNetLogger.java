/*
 *       _   _____            _      _   _          _   
 *      | | |  __ \          | |    | \ | |        | |  
 *      | | | |__) |   __ _  | | __ |  \| |   ___  | |_ 
 *  _   | | |  _  /   / _` | | |/ / | . ` |  / _ \ | __|
 * | |__| | | | \ \  | (_| | |   <  | |\  | |  __/ | |_ 
 *  \____/  |_|  \_\  \__,_| |_|\_\ |_| \_|  \___|  \__|
 *                                                  
 * The MIT License (MIT)
 *
 * Copyright (c) 2016, 2017 Trent "MarfGamer" Summerlin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.  
 */
package org.lunaris.network.raknet;

import org.lunaris.Lunaris;
import org.lunaris.network.raknet.client.RakNetClient;
import org.lunaris.network.raknet.server.RakNetServer;
import org.lunaris.util.logger.OwnLogger;

import java.util.logging.Logger;

/**
 * Used for logging in JRakNet.
 *
 * @author Trent "MarfGamer" Summerlin
 */
public class RakNetLogger {

    // Logger levels
    public static final int LEVEL_ERROR = 0;
    public static final int LEVEL_WARN = 1;
    public static final int LEVEL_INFO = 2;
    public static final int LEVEL_DEBUG = 3;
    private static final String[] LEVEL_NAMES = {"ERROR", "WARN", "INFO", "DEBUG"};
    private static final String[] LEVEL_SPACERS = new String[LEVEL_NAMES.length];
    private static final Logger logger = OwnLogger.getLogger("RakNet");

    // Logger data
    private static int loggerLevel = LEVEL_NAMES.length - 1;

    /**
     * Sets the logger level.
     *
     * @param level the logger level to use, use <code>-1</code> to enable logging
     *              for all levels.
     */
    public static void setLevel(int level) {
        // Set level
        loggerLevel = level;
        if (loggerLevel < 0 || loggerLevel >= LEVEL_NAMES.length) {
            loggerLevel = LEVEL_NAMES.length - 1;
        }

        // Get spacer size
        int spacerSize = 0;
        for (int i = 0; i <= loggerLevel; i++) {
            if (spacerSize < LEVEL_NAMES[i].length()) {
                spacerSize = LEVEL_NAMES[i].length();
            }
        }

        // Build spacers
        for (int i = 0; i <= loggerLevel; i++) {
            StringBuilder spacer = new StringBuilder();
            for (int j = 0; j < spacerSize - LEVEL_NAMES[i].length(); j++) {
                spacer.append(' ');
            }
            LEVEL_SPACERS[i] = spacer.toString();
        }
    }

    /**
     * @param level the logger level.
     * @return the name of the level.
     */
    public String getLevelName(int level) {
        if (level >= LEVEL_NAMES.length) {
            return null;
        }
        return LEVEL_NAMES[level];
    }
    
    public static boolean isLoggable(int level) {
        return RakNet.isLoggingEnabled() && loggerLevel >= level && level < LEVEL_NAMES.length;
    }
    
    /**
     * Logs a message if logging is enabled by the <code>RakNet</code> class and
     * the current level is greater than or equal to the current logging level.
     *
     * @param level   the level of the log.
     * @param message the message to log.
     */
    private static void log(int level, String name, String message) {
        if (isLoggable(level)) {
            logger.info(name + ": " + message);
            //			DateTime loggerDate = new DateTime(System.currentTimeMillis());
            //			@SuppressWarnings("resource") // Closing the streams would break the
            //											// console
            //			PrintStream logStream = (level != LEVEL_ERROR ? System.out : System.err);
            //			logStream.println("[" + LOGGER_DATE_FORMAT.format(loggerDate.getHourOfDay()) + ":"
            //					+ LOGGER_DATE_FORMAT.format(loggerDate.getMinuteOfHour()) + ":"
            //					+ LOGGER_DATE_FORMAT.format(loggerDate.getSecondOfMinute()) + "] ["
            //					+ LEVEL_NAMES[level].toUpperCase() + "]" + LEVEL_SPACERS[level] + " JRakNet " + message);
        }
    }

    /**
     * Logs a message with the severity level set to debug.
     *
     * @param name the name of the logger.
     * @param msg  the message to log.
     */
    public static void debug(String name, String msg) {
        log(LEVEL_DEBUG, name, msg);
    }

    /**
     * Logs a message with the severity level set to error.
     *
     * @param name the name of the logger.
     * @param msg  the message to log.
     */
    public static void error(String name, String msg) {
        log(LEVEL_ERROR, name, msg);
    }

    /**
     * Logs a message with the severity level set to warn.
     *
     * @param name the name of the logger.
     * @param msg  the message to log.
     */
    public static void warn(String name, String msg) {
        log(LEVEL_WARN, name, msg);
    }

    /**
     * Logs a message with the severity level set to info.
     *
     * @param name the name of the logger.
     * @param msg  the message to log.
     */
    public static void info(String name, String msg) {
        log(LEVEL_INFO, name, msg);
    }

    /**
     * Logs a message with the severity level set to debug.
     *
     * @param server the server that is logging.
     * @param msg    the message to log.
     */
    public static void debug(RakNetServer server, String msg) {
        log(LEVEL_DEBUG, "server", msg);
    }

    /**
     * Logs a message with the severity level set to error.
     *
     * @param server the server that is logging.
     * @param msg    the message to log.
     */
    public static void error(RakNetServer server, String msg) {
        log(LEVEL_ERROR, "server", msg);
    }

    /**
     * Logs a message with the severity level set to warn.
     *
     * @param server the server that is logging.
     * @param msg    the message to log.
     */
    public static void warn(RakNetServer server, String msg) {
        log(LEVEL_WARN, "server", msg);
    }

    /**
     * Logs a message with the severity level set to info.
     *
     * @param server the server that is logging.
     * @param msg    the message to log.
     */
    public static void info(RakNetServer server, String msg) {
        log(LEVEL_INFO, "server", msg);
    }

    /**
     * Logs a message with the severity level set to debug.
     *
     * @param client the client that is logging.
     * @param msg    the message to log.
     */
    public static void debug(RakNetClient client, String msg) {
        log(LEVEL_DEBUG, "client", msg);
    }

    /**
     * Logs a message with the severity level set to error.
     *
     * @param client the client that is logging.
     * @param msg    the message to log.
     */
    public static void error(RakNetClient client, String msg) {
        log(LEVEL_ERROR, "client", msg);
    }

    /**
     * Logs a message with the severity level set to warn.
     *
     * @param client the client that is logging.
     * @param msg    the message to log.
     */
    public static void warn(RakNetClient client, String msg) {
        log(LEVEL_WARN, "client", msg);
    }

    /**
     * Logs a message with the severity level set to info.
     *
     * @param client the client that is logging.
     * @param msg    the message to log.
     */
    public static void info(RakNetClient client, String msg) {
        log(LEVEL_INFO, "client", msg);
    }

}
