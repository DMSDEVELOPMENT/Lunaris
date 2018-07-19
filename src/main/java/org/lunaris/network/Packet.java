package org.lunaris.network;

import io.gomint.jraknet.PacketBuffer;

/**
 * Created by k.shandurenko on 19.07.2018
 */
public abstract class Packet {

    private PlayerConnection connection;

    public abstract byte getID();

    public abstract void read(PacketBuffer buffer);

    public abstract void write(PacketBuffer buffer);

    public PlayerConnection getConnection() {
        return this.connection;
    }

    public void setConnection(PlayerConnection connection) {
        this.connection = connection;
    }

}
