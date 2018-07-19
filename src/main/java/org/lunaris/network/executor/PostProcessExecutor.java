package org.lunaris.network.executor;

import org.lunaris.network.Packet;
import org.lunaris.network.PlayerConnection;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by k.shandurenko on 19.07.2018
 */
public class PostProcessExecutor {

    private final AtomicInteger connectionsInUse = new AtomicInteger(0);
    private final Executor executor = Executors.newSingleThreadExecutor();

    public void addWork(PlayerConnection connection, List<Packet> packets) {
        this.executor.execute(new PostProcessWorker(connection, packets));
    }

    public void addWork(PlayerConnection connection, Packet packet) {
        this.executor.execute(new PostProcessWorker(connection, packet));
    }

    public AtomicInteger getConnectionsInUse() {
        return this.connectionsInUse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostProcessExecutor that = (PostProcessExecutor) o;
        return Objects.equals(this.connectionsInUse, that.connectionsInUse) &&
                Objects.equals(this.executor, that.executor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.connectionsInUse, this.executor);
    }
}