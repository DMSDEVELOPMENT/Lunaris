package org.lunaris.network.protocol.packet;

import org.lunaris.entity.data.Attribute;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet1EUpdateAttributes extends MinePacket {

    private Attribute[] attributes;
    private long entityId;

    public Packet1EUpdateAttributes() {}

    public Packet1EUpdateAttributes(long entityId, Attribute... attributes) {
        this.entityId = entityId;
        this.attributes = attributes;
    }

    @Override
    public int getId() {
        return 0x1e;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.putVarLong(this.entityId);
        if(this.attributes == null)
            buffer.writeVarLong(0);
        else {
            buffer.writeVarLong(this.attributes.length);
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
