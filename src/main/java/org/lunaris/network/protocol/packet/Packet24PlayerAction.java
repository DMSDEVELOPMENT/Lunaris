package org.lunaris.network.protocol.packet;

import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

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
        this.entityId = buffer.getVarLong();
        this.action = Action.values()[buffer.getVarInt()];
        this.x = buffer.getVarInt();
        this.y = buffer.readVarInt();
        this.z = buffer.getVarInt();
        this.face = buffer.getVarInt();
    }

    @Override
    public void write(MineBuffer buffer) {

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
        START_BREAK,
        ABORT_BREAK,
        STOP_BREAK,
        GET_UPDATED_BLOCK,
        DROP_ITEM,
        STOP_SLEEPING,
        RESPAWN,
        JUMP,
        START_SPRINT,
        STOP_SPRINT,
        START_SNEAK,
        STOP_SNEAK,
        DIMENSION_CHANGE,
        DIMENSION_CHANGE_ACK,
        START_GLIDE,
        STOP_GLIDE,
        WORLD_IMMUTABLE,
        CONTINUE_BREAK,
        CHANGE_SKIN
    }

}
