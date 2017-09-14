package org.lunaris.network.protocol.packet;

import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet3BSetCommandsEnabled extends MinePacket {

    private boolean enabled;

    public Packet3BSetCommandsEnabled() {}

    public Packet3BSetCommandsEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public int getId() {
        return 0x3b;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeBoolean(this.enabled);
    }

}
