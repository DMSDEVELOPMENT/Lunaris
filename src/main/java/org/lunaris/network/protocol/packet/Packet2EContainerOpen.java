package org.lunaris.network.protocol.packet;

import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;
import org.lunaris.world.BlockVector;

/**
 * Created by RINES on 12.10.17.
 */
public class Packet2EContainerOpen extends MinePacket {

    private byte inventoryID;
    private byte type;
    private BlockVector position;
    private long entityID = -1L;

    public Packet2EContainerOpen() {}

    public Packet2EContainerOpen(byte inventoryID, byte type, BlockVector position, long entityID) {
        this.inventoryID = inventoryID;
        this.type = type;
        this.position = position;
        this.entityID = entityID;
    }

    @Override
    public int getId() {
        return 0x2e;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.inventoryID = buffer.readByte();
        this.type = buffer.readByte();
        this.position = buffer.readBlockVector();
        this.entityID = buffer.readVarLong();
    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeByte(this.inventoryID);
        buffer.writeByte(this.type);
        buffer.writeBlockVector(this.position);
        buffer.writeVarLong(this.entityID);
    }

}
