package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.network.Packet;
import org.lunaris.network_old.protocol.MineBuffer;

/**
 * Created by RINES on 13.09.17.
 */
public class Packet52ResourcePackDataInfo extends Packet {

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
    public byte getID() {
        return 0x52;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeString(this.packID);
        buffer.writeLInt(this.maxChunkSize);
        buffer.writeLInt(this.chunksAmount);
        buffer.writeLLong(this.compressedChunkSize);
        buffer.writeUnsignedVarInt(this.sha256.length);
        buffer.writeBytes(this.sha256);
    }
}
