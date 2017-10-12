package org.lunaris.network.protocol.packet;

import org.lunaris.entity.LPlayer;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;
import org.lunaris.util.math.Vector3f;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet13MovePlayer extends MinePacket {

    public static final int MODE_NORMAL = 0;
    public static final int MODE_RESET = 1;
    public static final int MODE_TELEPORT = 2;
    public static final int MODE_PITCH = 3; //facepalm Mojang

    private long entityId;
    private float x, y, z;
    private float yaw, headYaw, pitch;
    private int mode = MODE_RESET;
    private boolean onGround;
    private long ridingEntityId;
    private int unknown1, unknown2;

    public Packet13MovePlayer() {}

    public Packet13MovePlayer(LPlayer player) {
        this.entityId = player.getEntityID();
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
        this.yaw = player.getYaw();
        this.headYaw = player.getHeadYaw();
        this.pitch = player.getPitch();
        this.onGround = player.isOnGround();
    }

    public Packet13MovePlayer(long entityId, float x, float y, float z, float yaw, float pitch, float headYaw) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.headYaw = headYaw;
        this.pitch = pitch;
    }

    @Override
    public int getId() {
        return 0x13;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.entityId = buffer.readEntityRuntimeId();
        Vector3f position = buffer.readVector3f();
        this.x = position.x;
        this.y = position.y;
        this.z = position.z;
        this.pitch = buffer.readFloat();
        this.headYaw = buffer.readFloat();
        this.yaw = buffer.readFloat();
        this.mode = buffer.readByte();
        this.onGround = buffer.readBoolean();
        this.ridingEntityId = buffer.readVarLong();
        if(this.mode == MODE_TELEPORT) {
            this.unknown1 = buffer.readUnsignedInt();
            this.unknown2 = buffer.readUnsignedInt();
        }
    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeEntityRuntimeId(this.entityId);
        buffer.writeVector3f(this.x, this.y, this.z);
        buffer.writeFloat(this.pitch);
        buffer.writeFloat(this.headYaw);
        buffer.writeFloat(this.yaw);
        buffer.writeByte((byte) this.mode);
        buffer.writeBoolean(this.onGround);
        buffer.writeVarLong(this.ridingEntityId);
        if(this.mode == MODE_TELEPORT) {
            buffer.writeUnsignedInt(this.unknown1);
            buffer.writeUnsignedInt(this.unknown2);
        }
    }

    public long getEntityId() {
        return entityId;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getHeadYaw() {
        return headYaw;
    }

    public float getPitch() {
        return pitch;
    }

    public int getMode() {
        return mode;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public long getRidingEntityId() {
        return ridingEntityId;
    }

    public int getUnknown1() {
        return unknown1;
    }

    public int getUnknown2() {
        return unknown2;
    }

    public Packet13MovePlayer mode(int mode) {
        this.mode = mode;
        return this;
    }

}
