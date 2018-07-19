package org.lunaris.network_old.protocol.packet;

import org.lunaris.network_old.protocol.MineBuffer;
import org.lunaris.network_old.protocol.MinePacket;

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
