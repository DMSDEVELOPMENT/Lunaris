package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.network.Packet;

/**
 * Created by RINES on 05.10.17.
 */
public class Packet04EncryptionResponse extends Packet {

    @Override
    public byte getID() {
        return 0x04;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {

    }

}
