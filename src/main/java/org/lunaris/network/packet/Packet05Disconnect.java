package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.network.Packet;

/**
 * Created by RINES on 13.09.17.
 */
public class Packet05Disconnect extends Packet {

    private boolean hideDisconnectionScreen;
    private String reason;

    public Packet05Disconnect() {
        this(null);
    }

    public Packet05Disconnect(String reason) {
        if (reason == null || reason.isEmpty()) {
            this.hideDisconnectionScreen = false;
            reason = "Good bye";
        }
        this.reason = reason;
    }

    @Override
    public byte getID() {
        return 0x05;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeBoolean(this.hideDisconnectionScreen);
        buffer.writeString(this.reason);
    }

}
