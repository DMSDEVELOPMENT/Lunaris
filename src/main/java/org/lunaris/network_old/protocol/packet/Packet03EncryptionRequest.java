package org.lunaris.network_old.protocol.packet;

import org.lunaris.network_old.protocol.MineBuffer;
import org.lunaris.network_old.protocol.MinePacket;

/**
 * Created by RINES on 05.10.17.
 */
public class Packet03EncryptionRequest extends MinePacket {

    private String jwt;

    public Packet03EncryptionRequest() {}

    public Packet03EncryptionRequest(String jwt) {
        this.jwt = jwt;
    }

    @Override
    public int getId() {
        return 0x03;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.jwt = buffer.readString();
    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeString(this.jwt);
    }

}
