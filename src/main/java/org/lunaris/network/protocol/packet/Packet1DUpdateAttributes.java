package org.lunaris.network.protocol.packet;

import org.lunaris.entity.data.Attribute;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet1DUpdateAttributes extends MinePacket {

    private Attribute[] attributes;
    private long entityId;

    public Packet1DUpdateAttributes() {}

    public Packet1DUpdateAttributes(long entityId, Attribute... attributes) {
        this.entityId = entityId;
        this.attributes = attributes;
    }

    @Override
    public int getId() {
        return 0x1d;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeVarLong(this.entityId);
        if(this.attributes == null)
            buffer.writeUnsignedVarInt(0);
        else {
            buffer.writeUnsignedVarInt(this.attributes.length);
            for(Attribute a : this.attributes) {
                buffer.writeFloat(a.getMinValue());
                buffer.writeFloat(a.getMaxValue());
                buffer.writeFloat(a.getValue());
                buffer.writeFloat(a.getDefaultValue());
                buffer.writeString(a.getName());
            }
        }
    }

}
