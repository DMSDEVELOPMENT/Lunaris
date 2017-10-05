package org.lunaris.network.protocol.packet;

import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 05.10.17.
 */
public class Packet04EncryptionResponse extends MinePacket {

    @Override
    public int getId() {
        return 0x04;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {

    }

}
