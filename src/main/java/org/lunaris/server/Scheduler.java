package org.lunaris.server;

import org.lunaris.Lunaris;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by RINES on 12.09.17.
 */
public class Scheduler {
    public final static long ONE_TICK_IN_MILLIS = 50L;

    private static AtomicInteger ID_COUNTER = new AtomicInteger(0);

    private final ScheduledExecutorService serverThread = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService asyncExecutor = Executors.newScheduledThreadPool(5);

    public Scheduler() {

    }

    public Task run(Runnable runnable) {
        Task task = new Task(runnable);
        this.serverThread.submit(task::run);
        return task;
    }

    public Task schedule(Runnable runnable, long delay, TimeUnit unit) {
        Task task = new Task(runnable);
        this.serverThread.schedule(task::run, delay, unit);
        return task;
    }

    public Task scheduleRepeatable(Runnable runnable, long delay, long repeatDelay, TimeUnit unit) {
        Task task = new Task(runnable);
        this.serverThread.scheduleAtFixedRate(task::run, delay, repeatDelay, unit);
        return task;
    }

    public Task runAsync(Runnable runnable) {
        Task task = new Task(runnable);
        task.future = this.asyncExecutor.submit(task::run);
        return task;
    }

    public Task scheduleAsync(Runnable runnable, long delay, TimeUnit unit) {
        Task task = new Task(runnable);
        task.future = this.asyncExecutor.schedule(task::run, delay, unit);
        return task;
    }

    public Task scheduleRepeatableAsync(Runnable runnable, long delay, long repeatDelay, TimeUnit unit) {
        Task task = new Task(runnable);
        task.future = this.asyncExecutor.scheduleAtFixedRate(task::run, delay, repeatDelay, unit);
        return task;
    }

    public class Task {
        private final int id;
        private final Runnable runnable;
        private boolean finished;
        private Future<?> future;

        Task(Runnable runnable) {
            this.runnable = runnable;
            this.id = ID_COUNTER.getAndIncrement();
        }

        void run() {
            try {
                this.runnable.run();
            } catch (Exception ex) {
                Lunaris.getInstance().getLogger().error(ex, "Exception in task #" + this.id);
            }
            if (!isRepeatable())
                this.finished = true;
        }

        public int getId() {
            return this.id;
        }

        public boolean isFinished() {
            return this.finished;
        }

        public boolean isRepeatable() {
            return this.future instanceof RunnableScheduledFuture && ((RunnableScheduledFuture) this.future).isPeriodic();
        }

        /**
         * Отменяет задачу.
         *
         * @return false - если задача уже выполнилась или выполняется, true - если отменена.
         */
        public boolean cancel() {
            return this.future.cancel(false);
        }

        @Override
        public int hashCode() {
            return this.id;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Task && ((Task) obj).id == this.id;
        }
    }
}
