package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.network.Packet;

/**
 * Created by k.shandurenko on 19.07.2018
 */
public class PacketFEBatch extends Packet {

    private byte[] payload;

    public byte[] getPayload() {
        return this.payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    @Override
    public byte getID() {
        return -2;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.payload = new byte[buffer.getRemaining()];
        buffer.readBytes(this.payload);
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeBytes(this.payload);
    }

}
