package org.lunaris.network.packet;

import org.lunaris.network_old.protocol.MineBuffer;
import org.lunaris.network_old.protocol.MinePacket;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet45RequestChunkRadius extends MinePacket {

    private int radius;

    @Override
    public int getId() {
        return 0x45;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.radius = buffer.readVarInt();
    }

    @Override
    public void write(MineBuffer buffer) {

    }

    public int getRadius() {
        return radius;
    }

}
