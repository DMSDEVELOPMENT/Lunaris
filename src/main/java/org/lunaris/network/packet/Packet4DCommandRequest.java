package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.network.Packet;

import java.util.UUID;

/**
 * @author xtrafrancyz
 */
public class Packet4DCommandRequest extends Packet {
    public String command;
    public Type type;
    public UUID requestUUID;
    
    @Override
    public byte getID() {
        return 0x4d;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.command = buffer.readString();
        buffer.readByte(); //???
        this.requestUUID = buffer.readUUID();
        buffer.readByte();
        this.type = Type.values()[buffer.readByte()];
    }

    @Override
    public void write(PacketBuffer buffer) {

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
