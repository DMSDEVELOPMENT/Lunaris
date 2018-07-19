package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.api.entity.Gamemode;
import org.lunaris.network.Packet;

/**
 * Created by RINES on 01.10.17.
 */
public class Packet3ESetPlayerGameType extends Packet {

    private Gamemode gamemode;

    public Packet3ESetPlayerGameType() {}

    public Packet3ESetPlayerGameType(Gamemode gamemode) {
        this.gamemode = gamemode;
    }

    @Override
    public byte getID() {
        return 0x3e;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.gamemode = Gamemode.values()[buffer.readSignedVarInt()];
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeSignedVarInt(this.gamemode.ordinal());
    }

}
