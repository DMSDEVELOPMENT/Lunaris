package org.lunaris.network.protocol.packet;

import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 13.09.17.
 */
public class Packet02PlayStatus extends MinePacket {

    private Status status;

    public Packet02PlayStatus() {}

    public Packet02PlayStatus(Status status) {
        this.status = status;
    }

    @Override
    public int getId() {
        return 0x02;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.status = Status.values()[buffer.readInt()];
    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeInt(this.status.ordinal());
    }

    public Status getStatus() {
        return status;
    }

    public enum Status {
        LOGIN_SUCCESS,
        LOGIN_FAILED_CLIENT,
        LOGIN_FAILED_SERVER,
        PLAYER_RESPAWN,
        LOGIN_FAILED_INVALID_TENANT,
        LOGIN_FAILED_VANILLA_EDU,
        LOGIN_FAILED_EDU_VANILLA
    }

}
