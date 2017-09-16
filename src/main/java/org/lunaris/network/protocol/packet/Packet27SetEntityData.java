package org.lunaris.network.protocol.packet;

import org.lunaris.entity.data.*;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;
import org.lunaris.util.math.Vector3f;

import java.util.Map;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet27SetEntityData extends MinePacket {

    private long entityId;
    private EntityMetadata metadata;

    public Packet27SetEntityData() {}

    public Packet27SetEntityData(long entityId, EntityMetadata metadata) {
        this.entityId = entityId;
        this.metadata = metadata;
    }

    @Override
    public int getId() {
        return 0x27;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeVarLong(this.entityId);
        buffer.writeMetadata(this.metadata);
    }

    public EntityMetadata getMetadata() {
        return metadata;
    }

}
