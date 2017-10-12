package org.lunaris.event.network;

import org.lunaris.entity.LPlayer;
import org.lunaris.api.event.Cancellable;
import org.lunaris.api.event.Event;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 13.09.17.
 */
public class PacketSendingAsyncEvent extends Event implements Cancellable {

    private final LPlayer player;
    private final MinePacket packet;
    private boolean cancelled;

    public PacketSendingAsyncEvent(LPlayer player, MinePacket packet) {
        this.player = player;
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

    public LPlayer getPlayer() {
        return player;
    }

    public MinePacket getPacket() {
        return packet;
    }
}
