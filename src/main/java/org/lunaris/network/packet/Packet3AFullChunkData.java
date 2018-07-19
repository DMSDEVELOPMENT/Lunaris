package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;

import org.lunaris.network.Packet;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet3AFullChunkData extends Packet {

    private int x, z;
    private byte[] data;

    public Packet3AFullChunkData() {}

    public Packet3AFullChunkData(int x, int z, byte[] data) {
        this.x = x;
        this.z = z;
        this.data = data;
    }

    @Override
    public byte getID() {
        return 0x3a;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeSignedVarInt(this.x);
        buffer.writeSignedVarInt(this.z);
        buffer.writeUnsignedVarInt(this.data.length);
        buffer.writeBytes(this.data);
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

}
