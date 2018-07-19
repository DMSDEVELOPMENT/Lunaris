package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;

import org.lunaris.network.Packet;
import org.lunaris.network.util.SerializationUtil;
import org.lunaris.world.BlockVector;

/**
 * Created by RINES on 12.10.17.
 */
public class Packet2EContainerOpen extends Packet {

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
    public byte getID() {
        return 0x2e;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.inventoryID = buffer.readByte();
        this.type = buffer.readByte();
        this.position = SerializationUtil.readBlockVector(buffer);
        this.entityID = buffer.readSignedVarLong().longValue();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeByte(this.inventoryID);
        buffer.writeByte(this.type);
        SerializationUtil.writeBlockVector(this.position, buffer);
        buffer.writeSignedVarLong(this.entityID);
    }

}
