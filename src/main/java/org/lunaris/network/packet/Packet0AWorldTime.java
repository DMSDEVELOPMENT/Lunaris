package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.network.Packet;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet0AWorldTime extends Packet {

    private int time;

    public Packet0AWorldTime() {}

    public Packet0AWorldTime(int time) {
        this.time = time;
    }

    @Override
    public byte getID() {
        return 0x0a;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeSignedVarInt(this.time);
    }

    public int getTime() {
        return time;
    }

}
