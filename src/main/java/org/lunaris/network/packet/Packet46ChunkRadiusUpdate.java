package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.network.Packet;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet46ChunkRadiusUpdate extends Packet {

    private int radius;

    public Packet46ChunkRadiusUpdate(int radius) {
        this.radius = radius;
    }

    public Packet46ChunkRadiusUpdate() {}

    @Override
    public byte getID() {
        return 0x46;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.radius = buffer.readSignedVarInt();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeSignedVarInt(this.radius);
    }

}
