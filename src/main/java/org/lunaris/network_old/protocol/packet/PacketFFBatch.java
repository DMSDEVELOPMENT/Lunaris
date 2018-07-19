package org.lunaris.network_old.protocol.packet;

import org.lunaris.network_old.protocol.MineBuffer;
import org.lunaris.network_old.protocol.MinePacket;

/**
 * Created by RINES on 13.09.17.
 */
public class PacketFFBatch extends MinePacket {

    private byte[] payload;

    public PacketFFBatch() {}

    public PacketFFBatch(byte[] payload) {
        this.payload = payload;
    }

    @Override
    public int getId() {
        return 0xff;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.payload = buffer.readBytes(buffer.readableBytes());
    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeBytes(this.payload);
    }

}
