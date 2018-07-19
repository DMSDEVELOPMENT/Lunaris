package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;

import org.lunaris.network.Packet;

/**
 * Created by RINES on 16.09.17.
 */
public class Packet0ERemoveEntity extends Packet {

    private long entityId;

    public Packet0ERemoveEntity() {}

    public Packet0ERemoveEntity(long entityId) {
        this.entityId = entityId;
    }

    @Override
    public byte getID() {
        return 0x0e;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeSignedVarLong(this.entityId);
    }
}
