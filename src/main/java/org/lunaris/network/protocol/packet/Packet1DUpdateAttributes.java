package org.lunaris.network.protocol.packet;

import org.lunaris.entity.data.Attribute;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 14.09.17.
 */
//SOMETHING IS REALLY WRONG
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
        buffer.putVarLong(this.entityId);
        if(this.attributes == null)
            buffer.writeVarInt(0);
        else {
            buffer.writeVarInt(this.attributes.length);
            for(Attribute a : this.attributes) {
                buffer.writeLFloat(a.getMinValue());
                buffer.writeLFloat(a.getMaxValue());
                buffer.writeLFloat(a.getValue());
                buffer.writeLFloat(a.getDefaultValue());
                buffer.writeStringUnlimited(a.getName());
            }
        }
    }

}
