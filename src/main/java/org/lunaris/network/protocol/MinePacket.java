package org.lunaris.network.protocol;

import org.lunaris.entity.LPlayer;

/**
 * Created by RINES on 13.09.17.
 */
public abstract class MinePacket {

    private LPlayer player;

    public byte getByteId() {
        return (byte) getId();
    }

    public abstract int getId();

    public abstract void read(MineBuffer buffer);

    public abstract void write(MineBuffer buffer);

    public LPlayer getPlayer() {
        return this.player;
    }

    public void setPlayer(LPlayer player) {
        this.player = player;
    }
}
