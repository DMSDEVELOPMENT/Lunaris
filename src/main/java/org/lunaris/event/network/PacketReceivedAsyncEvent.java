package org.lunaris.event.network;

import org.lunaris.api.event.Cancellable;
import org.lunaris.api.event.Event;
import org.lunaris.network.Packet;

/**
 * Created by RINES on 13.09.17.
 */
public class PacketReceivedAsyncEvent extends Event implements Cancellable {

    private final Packet packet;

    private boolean cancelled;

    public PacketReceivedAsyncEvent(Packet packet) {
        this.packet = packet;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public Packet getPacket() {
        return packet;
    }
}
