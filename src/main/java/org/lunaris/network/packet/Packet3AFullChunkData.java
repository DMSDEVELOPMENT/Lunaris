package org.lunaris.network.packet;

import org.lunaris.network_old.protocol.MineBuffer;
import org.lunaris.network_old.protocol.MinePacket;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet3AFullChunkData extends MinePacket {

    private int x, z;
    private byte[] data;

    public Packet3AFullChunkData() {}

    public Packet3AFullChunkData(int x, int z, byte[] data) {
        this.x = x;
        this.z = z;
        this.data = data;
    }

    @Override
    public int getId() {
        return 0x3a;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeVarInt(this.x);
        buffer.writeVarInt(this.z);
        buffer.writeByteArray(this.data);
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

}
