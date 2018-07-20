package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.network.Packet;

/**
 * Created by RINES on 04.10.17.
 */
public class Packet11PickupItem extends Packet {

    private long itemEntityID;
    private long playerEntityID;

    public Packet11PickupItem() {
    }

    public Packet11PickupItem(long itemEntityID, long playerEntityID) {
        this.itemEntityID = itemEntityID;
        this.playerEntityID = playerEntityID;
    }

    @Override
    public byte getID() {
        return 0x11;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUnsignedVarLong(this.itemEntityID);
        buffer.writeUnsignedVarLong(this.playerEntityID);
    }

}
