package org.lunaris.network_old.protocol.packet;

import org.lunaris.network_old.protocol.MineBuffer;
import org.lunaris.network_old.protocol.MinePacket;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet0ASetTime extends MinePacket {

    private int time;

    public Packet0ASetTime() {}

    public Packet0ASetTime(int time) {
        this.time = time;
    }

    @Override
    public int getId() {
        return 0x0a;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeVarInt(this.time);
    }

    public int getTime() {
        return time;
    }

}
