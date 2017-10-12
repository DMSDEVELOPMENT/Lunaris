package org.lunaris.api.event.player;

import org.lunaris.api.event.Event;

import java.net.InetSocketAddress;

/**
 * Событие подключения игрока по RakNet протоколу.
 * Created by RINES on 13.09.17.
 */
public class PlayerConnectAsyncEvent extends Event {

    private final InetSocketAddress address;

    public PlayerConnectAsyncEvent(InetSocketAddress address) {
        this.address = address;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

}
