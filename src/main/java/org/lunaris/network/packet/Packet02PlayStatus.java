package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.network.Packet;

/**
 * Created by k.shandurenko on 19.07.2018
 */
public class Packet02PlayStatus extends Packet {

    private Status status;

    public Packet02PlayStatus(Status status) {
        this.status = status;
    }

    public Packet02PlayStatus() {
    }

    @Override
    public byte getID() {
        return 0x02;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.status = Status.values()[buffer.readInt()];
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeInt(this.status.ordinal());
    }

    public Status getStatus() {
        return this.status;
    }

    public enum Status {
        LOGIN_SUCCESS,
        LOGIN_FAILED_CLIENT,
        LOGIN_FAILED_SERVER,
        RESPAWN
    }

}
