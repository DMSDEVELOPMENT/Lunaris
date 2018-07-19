package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.network.Packet;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet3BSetCommandsEnabled extends Packet {

    private boolean enabled;

    public Packet3BSetCommandsEnabled() {}

    public Packet3BSetCommandsEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public byte getID() {
        return 0x3b;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeBoolean(this.enabled);
    }

}
