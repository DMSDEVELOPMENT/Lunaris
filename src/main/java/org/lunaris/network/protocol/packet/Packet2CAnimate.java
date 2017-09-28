package org.lunaris.network.protocol.packet;

import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 28.09.17.
 */
public class Packet2CAnimate extends MinePacket {

    private long entityID;
    private int action;
    private float unknown;

    @Override
    public int getId() {
        return 0x2c;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.action = buffer.readVarInt();
        this.entityID = buffer.readEntityRuntimeId();
        if((this.action & 0x80) != 0)
            this.unknown = buffer.readFloat();
    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeVarInt(this.action);
        buffer.writeEntityRuntimeId(this.entityID);
        if((this.action & 0x80) != 0)
            buffer.writeFloat(this.unknown);
    }

}
