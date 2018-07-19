package org.lunaris.api.event.player;

import org.lunaris.api.event.Cancellable;
import org.lunaris.api.event.Event;

import java.net.InetSocketAddress;

/**
 * Событие подключения игрока по RakNet протоколу.
 * Created by RINES on 13.09.17.
 */
public class PlayerConnectAsyncEvent extends Event implements Cancellable {

    private final InetSocketAddress address;
    private boolean cancelled;

    public PlayerConnectAsyncEvent(InetSocketAddress address) {
        this.address = address;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
}
