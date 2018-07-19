package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;

import org.lunaris.entity.data.Attribute;
import org.lunaris.network.Packet;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet1DUpdateAttributes extends Packet {

    private long entityId;
    private Attribute[] attributes;

    public Packet1DUpdateAttributes() {}

    public Packet1DUpdateAttributes(long entityId, Attribute... attributes) {
        this.entityId = entityId;
        this.attributes = attributes;
    }

    @Override
    public byte getID() {
        return 0x1d;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUnsignedVarLong(this.entityId);
        if (this.attributes == null)
            buffer.writeUnsignedVarInt(0);
        else {
            buffer.writeUnsignedVarInt(this.attributes.length);
            for (Attribute a : this.attributes) {
                buffer.writeLFloat(a.getMinValue());
                buffer.writeLFloat(a.getMaxValue());
                buffer.writeLFloat(a.getValue());
                buffer.writeLFloat(a.getDefaultValue());
                buffer.writeString(a.getName());
            }
        }
    }

}
