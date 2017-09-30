package org.lunaris.util.logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.LogRecord;

/**
 * @author RinesThaix
 */
public class LogDispatcher extends Thread {

    private final OwnLogger logger;
    private final BlockingQueue<LogRecord> queue = new LinkedBlockingQueue<>();

    public LogDispatcher(OwnLogger logger) {
        super("Lunaris Logger Thread");
        this.setDaemon(true);
        this.logger = logger;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            LogRecord record;
            try {
                record = queue.take();
            } catch (InterruptedException ex) {
                continue;
            }

            logger.doLog(record);
        }
        for (LogRecord record : queue) {
            logger.doLog(record);
        }
    }

    public void queue(LogRecord record) {
        if (!isInterrupted()) {
            queue.add(record);
        }
    }
}
