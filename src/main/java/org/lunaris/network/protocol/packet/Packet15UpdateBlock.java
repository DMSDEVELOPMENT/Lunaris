package org.lunaris.network.protocol.packet;

import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 24.09.17.
 */
public class Packet15UpdateBlock extends MinePacket {
    @Override
    public int getId() {
        return 0x15;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {

    }
}
