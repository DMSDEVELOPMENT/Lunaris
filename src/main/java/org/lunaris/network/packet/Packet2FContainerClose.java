package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.network.Packet;

/**
 * Created by RINES on 12.10.17.
 */
public class Packet2FContainerClose extends Packet {

    private byte inventoryID;

    public Packet2FContainerClose() {}

    public Packet2FContainerClose(byte inventoryID) {
        this.inventoryID = inventoryID;
    }

    public byte getInventoryID() {
        return this.inventoryID;
    }

    @Override
    public byte getID() {
        return 0x2f;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.inventoryID = buffer.readByte();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeByte(this.inventoryID);
    }

}
