package org.lunaris.network.protocol.packet;

import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

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
        buffer.putVarInt(this.x);
        buffer.putVarInt(this.z);
        buffer.writeVarInt(this.data.length);
        buffer.writeBytes(this.data);
    }

}
