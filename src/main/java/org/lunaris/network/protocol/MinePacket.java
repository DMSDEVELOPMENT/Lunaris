package org.lunaris.network.protocol;

import org.lunaris.entity.Player;

/**
 * Created by RINES on 13.09.17.
 */
public abstract class MinePacket {

    private Player player;

    public byte getByteId() {
        return (byte) getId();
    }

    public abstract int getId();

    public abstract void read(MineBuffer buffer);

    public abstract void write(MineBuffer buffer);

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
