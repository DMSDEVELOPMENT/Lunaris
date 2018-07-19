package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.entity.data.*;
import org.lunaris.network.Packet;
import org.lunaris.network.util.SerializationUtil;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet27SetEntityData extends Packet {

    private long entityId;
    private EntityMetadata metadata;

    public Packet27SetEntityData() {}

    public Packet27SetEntityData(long entityId, EntityMetadata metadata) {
        this.entityId = entityId;
        this.metadata = metadata;
    }

    @Override
    public byte getID() {
        return 0x27;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUnsignedVarLong(this.entityId);
        SerializationUtil.writeMetadata(this.metadata, buffer);
    }

    public EntityMetadata getMetadata() {
        return metadata;
    }

}
