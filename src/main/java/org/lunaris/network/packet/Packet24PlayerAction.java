package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.network.Packet;
import org.lunaris.network.util.SerializationUtil;
import org.lunaris.world.BlockVector;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet24PlayerAction extends Packet {

    private long entityId;
    private Action action;
    private int x, y, z, face;

    @Override
    public byte getID() {
        return 0x24;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.entityId = buffer.readUnsignedVarLong();
        this.action = Action.values()[buffer.readSignedVarInt()];
        BlockVector v = SerializationUtil.readBlockVector(buffer);
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.face = buffer.readSignedVarInt();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUnsignedVarLong(this.entityId);
        buffer.writeSignedVarInt(this.action.ordinal());
        SerializationUtil.writeBlockVector(new BlockVector(this.x, this.y, this.z), buffer);
        buffer.writeSignedVarInt(this.face);
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
        START_SLEEPING,
        STOP_SLEEPING,
        RESPAWN,
        JUMP,
        START_SPRINT,
        STOP_SPRINT,
        START_SNEAK,
        STOP_SNEAK,
        DIMENSION_CHANGE_REQUEST,
        DIMENSION_CHANGE_ACK,
        START_GLIDE,
        STOP_GLIDE,
        BUILD_DENIED,
        CONTINUE_BREAK
    }

}
