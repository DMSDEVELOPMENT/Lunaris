package org.lunaris.network.packet;

import org.lunaris.network_old.protocol.MineBuffer;
import org.lunaris.network_old.protocol.MinePacket;

/**
 * @author xtrafrancyz
 */
public class Packet4DCommandRequest extends MinePacket {
    public String command;
    public Type type;
    public String requestId;
    public long playerUniqueId;
    
    @Override
    public int getId() {
        return 0x4d;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.command = buffer.readString();
        this.type = Type.values()[buffer.readByte()];
        this.requestId = buffer.readString();
        this.playerUniqueId = buffer.readVarLong();
    }

    @Override
    public void write(MineBuffer buffer) {

    }
    
    public enum Type {
        PLAYER,
        COMMAND_BLOCK,
        MINECART_COMMAND_BLOCK,
        DEV_CONSOLE,
        AUTOMATION_PLAYER,
        CLIENT_AUTOMATION,
        DEDICATED_SERVER,
        ENTITY,
        VIRTUAL,
        GAME_ARGUMENT,
        INTERNAL,
        UNKNOWN,
    }
}
