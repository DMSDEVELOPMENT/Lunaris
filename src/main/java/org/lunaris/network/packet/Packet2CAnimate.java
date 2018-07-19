package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.network.Packet;

/**
 * Created by RINES on 28.09.17.
 */
public class Packet2CAnimate extends Packet {

    private long entityID;
    private int action;
    private float unknown;

    @Override
    public byte getID() {
        return 0x2c;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.action = buffer.readSignedVarInt();
        this.entityID = buffer.readUnsignedVarLong();
        if ((this.action & 0x80) != 0)
            this.unknown = buffer.readFloat();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeSignedVarInt(this.action);
        buffer.writeUnsignedVarLong(this.entityID);
        if ((this.action & 0x80) != 0)
            buffer.writeFloat(this.unknown);
    }

}
