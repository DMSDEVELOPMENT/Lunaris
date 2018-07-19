package org.lunaris.network.packet;

import org.lunaris.network_old.protocol.MineBuffer;
import org.lunaris.network_old.protocol.MinePacket;

/**
 * Created by RINES on 12.10.17.
 */
public class Packet2FContainerClose extends MinePacket {

    private byte inventoryID;

    public Packet2FContainerClose() {}

    public Packet2FContainerClose(byte inventoryID) {
        this.inventoryID = inventoryID;
    }

    public byte getInventoryID() {
        return this.inventoryID;
    }

    @Override
    public int getId() {
        return 0x2f;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.inventoryID = buffer.readByte();
    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeByte(this.inventoryID);
    }

}
