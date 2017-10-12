package org.lunaris.event.network;

import org.lunaris.api.event.Cancellable;
import org.lunaris.api.event.Event;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 13.09.17.
 */
public class PacketReceivedAsyncEvent extends Event implements Cancellable {

    private final MinePacket packet;

    private boolean cancelled;

    public PacketReceivedAsyncEvent(MinePacket packet) {
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

    public MinePacket getPacket() {
        return packet;
    }
}
