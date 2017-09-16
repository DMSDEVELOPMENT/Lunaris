package org.lunaris.network.protocol.packet;

import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 16.09.17.
 */
public class Packet0ERemoveEntity extends MinePacket {

    private long entityId;

    public Packet0ERemoveEntity() {}

    public Packet0ERemoveEntity(long entityId) {
        this.entityId = entityId;
    }

    @Override
    public int getId() {
        return 0x0e;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeEntityRuntimeId(this.entityId);
    }
}
