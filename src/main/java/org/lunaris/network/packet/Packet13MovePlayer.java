package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.entity.LPlayer;
import org.lunaris.network.Packet;
import org.lunaris.network.util.SerializationUtil;
import org.lunaris.util.math.Vector3f;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet13MovePlayer extends Packet {

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

    public Packet13MovePlayer() {
    }

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
    public byte getID() {
        return 0x13;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.entityId = buffer.readUnsignedVarLong();
        Vector3f position = SerializationUtil.readVector3f(buffer);
        this.x = position.x;
        this.y = position.y;
        this.z = position.z;
        position = SerializationUtil.readVector3f(buffer);
        this.pitch = position.x;
        this.headYaw = position.y;
        this.yaw = position.z;
        this.mode = buffer.readByte();
        this.onGround = buffer.readBoolean();
        this.ridingEntityId = buffer.readUnsignedVarLong();
        if (this.mode == MODE_TELEPORT) {
            this.unknown1 = buffer.readLInt();
            this.unknown2 = buffer.readLInt();
        }
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUnsignedVarLong(this.entityId);
        SerializationUtil.writeVector3f(new Vector3f(this.x, this.y, this.z), buffer);
        SerializationUtil.writeVector3f(new Vector3f(this.pitch, this.headYaw, this.yaw), buffer);
        buffer.writeByte((byte) this.mode);
        buffer.writeBoolean(this.onGround);
        buffer.writeUnsignedVarLong(this.ridingEntityId);
        if (this.mode == MODE_TELEPORT) {
            buffer.writeLInt(this.unknown1);
            buffer.writeLInt(this.unknown2);
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
