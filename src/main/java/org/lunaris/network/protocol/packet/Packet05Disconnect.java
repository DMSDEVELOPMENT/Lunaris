package org.lunaris.network.protocol.packet;

import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 13.09.17.
 */
public class Packet05Disconnect extends MinePacket {

    private boolean hideReason;
    private String reason;

    public Packet05Disconnect() {
        this(null);
    }

    public Packet05Disconnect(String reason) {
        if(reason == null || reason.isEmpty())
            this.hideReason = true;
        this.reason = reason;
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeBoolean(this.hideReason);
        if(!this.hideReason)
            buffer.writeString(this.reason);
    }

}
