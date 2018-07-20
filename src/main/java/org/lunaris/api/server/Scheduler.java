package org.lunaris.api.server;

import co.aikar.timings.Timings;
import org.lunaris.LunarisServer;
import org.lunaris.api.util.Internal;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by RINES on 12.09.17.
 */
public class Scheduler {

    public final static long ONE_TICK_IN_MILLIS = 50L;

    private static AtomicInteger ID_COUNTER = new AtomicInteger(0);

    private final ScheduledExecutorService serverThread = createScheduledExecutor("Server Thread", 1);
    private final ScheduledExecutorService asyncExecutor = createScheduledExecutor("Async Task Pool", 5);

    public Scheduler(LunarisServer server) {
        scheduleRepeatable(() -> {
            Timings.fullServerTickTimer.stopTiming();
            Timings.fullServerTickTimer.startTiming();
        }, 0L, 40L, TimeUnit.MILLISECONDS);
    }

    /**
     * Invoke runnable synchronously with main world thread.
     *
     * @param runnable the runnable to be executed.
     * @return task, corresponding to described execution.
     */
    public Task run(Runnable runnable) {
        Task task = new Task(runnable);
        task.future = this.serverThread.submit(task::run);
        return task;
    }

    /**
     * Invoke runnable synchronously with main world thread, but delay it for given amount of time.
     *
     * @param runnable the runnable to be executed.
     * @param delay    amount of time for execution delay.
     * @param unit     unit of time.
     * @return task, corresponding to described execution.
     */
    public Task schedule(Runnable runnable, long delay, TimeUnit unit) {
        Task task = new Task(runnable);
        task.future = this.serverThread.schedule(task::run, delay, unit);
        return task;
    }

    /**
     * Invoke runnable synchronously with main world thread.
     * Delay it for given amount of time and run it repeatedly with given repeat delay.
     *
     * @param runnable    the runnable to be executed.
     * @param delay       amount of time for execution delay.
     * @param repeatDelay amount of time for repeatedly execution delay.
     * @param unit        unit of time.
     * @return task, corresponding to described execution.
     */
    public Task scheduleRepeatable(Runnable runnable, long delay, long repeatDelay, TimeUnit unit) {
        Task task = new Task(runnable);
        task.future = this.serverThread.scheduleAtFixedRate(task::run, delay, repeatDelay, unit);
        return task;
    }

    /**
     * Invoke runnable asynchronously.
     *
     * @param runnable the runnable to be executed.
     * @return task, corresponding to described execution.
     */
    public Task runAsync(Runnable runnable) {
        Task task = new Task(runnable);
        task.future = this.asyncExecutor.submit(task::run);
        return task;
    }

    public ScheduledExecutorService getAsyncExecutor() {
        return this.asyncExecutor;
    }

    /**
     * Invoke runnable asynchronously with given delay.
     *
     * @param runnable the runnable to be executed.
     * @param delay    amount of time for execution delay.
     * @param unit     unit of time.
     * @return task, corresponding to described execution.
     * @see Scheduler#schedule(Runnable, long, TimeUnit)
     */
    public Task scheduleAsync(Runnable runnable, long delay, TimeUnit unit) {
        Task task = new Task(runnable);
        task.future = this.asyncExecutor.schedule(task::run, delay, unit);
        return task;
    }

    /**
     * Invoke runnable asynchronously with given delay and repeat-delay.
     *
     * @param runnable    the runnable to be executed.
     * @param delay       amount of time for execution delay.
     * @param repeatDelay amount of time for repeatedly execution delay.
     * @param unit        unit of time.
     * @return task, corresponding to described execution.
     * @see Scheduler#scheduleRepeatable(Runnable, long, long, TimeUnit)
     */
    public Task scheduleRepeatableAsync(Runnable runnable, long delay, long repeatDelay, TimeUnit unit) {
        Task task = new Task(runnable);
        task.future = this.asyncExecutor.scheduleAtFixedRate(task::run, delay, repeatDelay, unit);
        return task;
    }

    @Internal
    public static ScheduledExecutorService createScheduledExecutor(String name, int threads) {
        if (threads == 1) {
            return Executors.newScheduledThreadPool(1, r -> new Thread(r, name));
        } else {
            return Executors.newScheduledThreadPool(threads, new ThreadFactory() {
                private final AtomicInteger threadNumber = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, name + " #" + threadNumber.getAndIncrement());
                }
            });
        }
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
                LunarisServer.getInstance().getLogger().error(ex, "Exception in task #" + this.id);
            }
            if (!isRepeatable())
                this.finished = true;
        }

        /**
         * Get this task id.
         *
         * @return this task id.
         */
        public int getId() {
            return this.id;
        }

        /**
         * Check whether this task is finished.
         *
         * @return if this task is finished.
         */
        public boolean isFinished() {
            return this.finished;
        }

        /**
         * Check whether this task corresponds to repeatable executions.
         *
         * @return if this task corresponds to repeatable executions.
         */
        public boolean isRepeatable() {
            return this.future instanceof RunnableScheduledFuture && ((RunnableScheduledFuture) this.future).isPeriodic();
        }

        /**
         * Cancels the task.
         *
         * @return false, whether the task is already finished or is in progress; true otherwise.
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
