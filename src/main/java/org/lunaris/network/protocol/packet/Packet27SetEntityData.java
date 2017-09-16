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
        Map<Integer, EntityData> map = this.metadata.getMap();
        buffer.writeUnsignedVarInt(map.size());
        map.forEach((id, data) -> {
            buffer.writeUnsignedVarInt(id);
            buffer.writeUnsignedVarInt(data.getTypeId());
            switch(data.getType()) {
                case BYTE:
                    buffer.writeByte(((ByteEntityData) data).getData().byteValue());
                    break;
                case SHORT:
                    buffer.writeUnsignedShort((short) (int) ((ShortEntityData) data).getData());
                    break;
                case INT:
                    buffer.writeVarInt(((IntEntityData) data).getData());
                    break;
                case FLOAT:
                    buffer.writeFloat(((FloatEntityData) data).getData());
                    break;
                case STRING:
                    buffer.writeString(((StringEntityData) data).getData());
                    break;
                case SLOT:
                    SlotEntityData slot = (SlotEntityData) data;
                    buffer.writeUnsignedShort((short) slot.blockId);
                    buffer.writeByte((byte) slot.meta);
                    buffer.writeUnsignedShort((short) slot.count);
                    break;
                case POS:
                    IntPositionEntityData pos = (IntPositionEntityData) data;
                    buffer.writeVarInt(pos.x);
                    buffer.writeByte((byte) pos.y);
                    buffer.writeVarInt(pos.z);
                    break;
                case LONG:
                    buffer.writeVarLong(((LongEntityData) data).getData());
                    break;
                case VECTOR3F:
                    Vector3f vector = ((Vector3fEntityData) data).getData();
                    buffer.writeVector3f(vector.x, vector.y, vector.z);
                    break;
            }
        });
    }

    public EntityMetadata getMetadata() {
        return metadata;
    }

}
