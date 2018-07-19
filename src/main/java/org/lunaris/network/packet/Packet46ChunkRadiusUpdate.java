package org.lunaris.network.packet;

import org.lunaris.network_old.protocol.MineBuffer;
import org.lunaris.network_old.protocol.MinePacket;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet46ChunkRadiusUpdate extends MinePacket {

    private int radius;

    public Packet46ChunkRadiusUpdate(int radius) {
        this.radius = radius;
    }

    public Packet46ChunkRadiusUpdate() {}

    @Override
    public int getId() {
        return 0x46;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeVarInt(this.radius);
    }

}
