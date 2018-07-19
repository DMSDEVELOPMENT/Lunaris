package org.lunaris.network_old.protocol.packet;

import org.lunaris.network_old.protocol.MineBuffer;
import org.lunaris.network_old.protocol.MinePacket;

/**
 * Created by RINES on 04.10.17.
 */
public class Packet11PickupItem extends MinePacket {

    private long itemEntityID;
    private long playerEntityID;

    public Packet11PickupItem() {}

    public Packet11PickupItem(long itemEntityID, long playerEntityID) {
        this.itemEntityID = itemEntityID;
        this.playerEntityID = playerEntityID;
    }

    @Override
    public int getId() {
        return 0x11;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeEntityRuntimeId(this.itemEntityID);
        buffer.writeEntityRuntimeId(this.playerEntityID);
    }

}
