package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.network.Packet;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet2DRespawn extends Packet {

    private float x, y, z;

    public Packet2DRespawn() {}

    public Packet2DRespawn(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public byte getID() {
        return 0x2d;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeLFloat(this.x);
        buffer.writeLFloat(this.y);
        buffer.writeLFloat(this.z);
    }

}
