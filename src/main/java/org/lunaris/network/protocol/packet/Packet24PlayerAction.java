package org.lunaris.network.protocol.packet;

import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;
import org.lunaris.world.BlockVector;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet24PlayerAction extends MinePacket {

    private long entityId;
    private Action action;
    private int x, y, z, face;

    @Override
    public int getId() {
        return 0x24;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.entityId = buffer.readEntityRuntimeId();
        this.action = Action.values()[buffer.readVarInt()];
        BlockVector v = buffer.readBlockVector();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.face = buffer.readVarInt();
    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeEntityRuntimeId(this.entityId);
        buffer.writeVarInt(this.action.ordinal());
        buffer.writeBlockVector(this.x, this.y, this.z);
        buffer.writeVarInt(this.face);
    }

    public long getEntityId() {
        return entityId;
    }

    public Action getAction() {
        return action;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getFace() {
        return face;
    }

    public enum Action {
        UNKNOWN,
        START_BREAK,
        ABORT_BREAK,
        STOP_BREAK,
        GET_UPDATED_BLOCK,
        DROP_ITEM,
        STOP_SLEEPING,
        RESPAWN,
        JUMP, //+
        START_SPRINT, //+
        STOP_SPRINT, //+
        START_SNEAK, //+
        STOP_SNEAK, //+
        DIMENSION_CHANGE_ACK,
        START_GLIDE,
        STOP_GLIDE,
        WORLD_IMMUTABLE,
        CONTINUE_BREAK, //Что-то с ломанием блоков
        CHANGE_SKIN
    }

}
