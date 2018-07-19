package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.network.Packet;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet45RequestChunkRadius extends Packet {

    private int radius;

    @Override
    public byte getID() {
        return 0x45;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.radius = buffer.readSignedVarInt();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeSignedVarInt(this.radius);
    }

    public int getRadius() {
        return radius;
    }

}
