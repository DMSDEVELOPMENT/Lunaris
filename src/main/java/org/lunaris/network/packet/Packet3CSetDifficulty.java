package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.api.world.Difficulty;
import org.lunaris.network.Packet;

/**
 * Created by k.shandurenko on 20.07.2018
 */
public class Packet3CSetDifficulty extends Packet {

    private Difficulty difficulty;

    public Packet3CSetDifficulty() {}

    public Packet3CSetDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public byte getID() {
        return 0x3C;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUnsignedVarInt(this.difficulty.ordinal());
    }

}
