package org.lunaris.server;

import org.lunaris.util.exception.TaskInvocationException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by RINES on 12.09.17.
 */
public class Scheduler {

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final Unsafe unsafe = new Unsafe();

    private final IServer server;

    private final List<Runnable> syncTasks = new ArrayList<>();

    public Scheduler(IServer server) {
        this.server = server;
        schedule(() -> {
            synchronized(syncTasks) {
                List<Runnable> copied = new ArrayList<>(this.syncTasks);
                this.syncTasks.clear();
                copied.forEach(task -> {
                    try {
                        task.run();
                    }catch(Exception ex) {
                        new TaskInvocationException(ex).printStackTrace();
                    }
                });
            }
        }, 0L, 50L, TimeUnit.MILLISECONDS);
    }

    public Unsafe getUnsafe() {
        return this.unsafe;
    }

    public void addSyncTask(Runnable run) {
        synchronized(syncTasks) {
            this.syncTasks.add(run);
        }
    }

    public void schedule(Runnable task, long delay, TimeUnit unit) {
        this.executor.schedule(() -> {
            try {
                task.run();
            }catch(Exception ex) {
                throw new TaskInvocationException(ex);
            }
        }, delay, unit);
    }

    public void schedule(Runnable task, long delay, long repeatDelay, TimeUnit unit) {
        this.executor.scheduleAtFixedRate(() -> {
            try {
                task.run();
            } catch (Exception ex) {
                throw new TaskInvocationException(ex);
            }
        }, delay, repeatDelay, unit);
    }

    public void schedule(Runnable task) {
        this.executor.execute(() -> {
            try {
                task.run();
            }catch(Exception ex) {
                throw new TaskInvocationException(ex);
            }
        });
    }

    public void runAsync(Runnable task) {
        Thread t = new Thread(() -> {
            try {
                task.run();
            }catch(Exception ex) {
                throw new TaskInvocationException(ex);
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        }catch(InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public class Unsafe {

        public Executor getExecutor() {
            return Scheduler.this.executor;
        }

    }

}
