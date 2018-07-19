package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.network.Packet;

/**
 * Created by RINES on 05.10.17.
 */
public class Packet03EncryptionRequest extends Packet {

    private String jwt;

    public Packet03EncryptionRequest() {}

    public Packet03EncryptionRequest(String jwt) {
        this.jwt = jwt;
    }

    @Override
    public byte getID() {
        return 0x03;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.jwt = buffer.readString();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeString(this.jwt);
    }

}
