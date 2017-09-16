package org.lunaris.network.protocol.packet;

import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 13.09.17.
 */
public class Packet52ResourcePackDataInfo extends MinePacket {

    private String packID;
    private int maxChunkSize;
    private int chunksAmount;
    private long compressedChunkSize;
    private byte[] sha256;

    public Packet52ResourcePackDataInfo() {}

    public Packet52ResourcePackDataInfo(String packID, int maxChunkSize, int chunksAmount, long compressedChunkSize, byte[] sha256) {
        this.packID = packID;
        this.maxChunkSize = maxChunkSize;
        this.chunksAmount = chunksAmount;
        this.compressedChunkSize = compressedChunkSize;
        this.sha256 = sha256;
    }

    @Override
    public int getId() {
        return 0x52;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeString(this.packID);
        buffer.writeUnsignedInt(this.maxChunkSize);
        buffer.writeUnsignedInt(this.chunksAmount);
        buffer.writeUnsignedLong(this.compressedChunkSize);
        buffer.writeByteArray(this.sha256);
    }
}
