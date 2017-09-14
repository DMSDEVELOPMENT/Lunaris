package org.lunaris.network.protocol.packet;

import org.lunaris.entity.Player;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;
import org.lunaris.util.math.Vector3f;
import org.lunaris.world.Location;

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
    private int mode = MODE_NORMAL;
    private boolean onGround;
    private long ridingEntityId;
    private int unknown1, unknown2;

    public Packet13MovePlayer() {}

    public Packet13MovePlayer(Player player) {
        this.entityId = player.getEntityID();
        Location loc = player.getLocation();
        this.x = (float) loc.getX();
        this.y = (float) loc.getY();
        this.z = (float) loc.getZ();
        this.yaw = this.headYaw = (float) loc.getYaw();
        this.pitch = (float) loc.getPitch();
        this.onGround = true;
    }

    @Override
    public int getId() {
        return 0x13;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.entityId = buffer.getVarLong();
        Vector3f position = buffer.getVector3f();
        this.x = position.x;
        this.y = position.y;
        this.z = position.z;
        this.yaw = buffer.getLFloat();
        this.headYaw = buffer.getLFloat();
        this.pitch = buffer.getLFloat();
        this.mode = buffer.readByte();
        this.onGround = buffer.readBoolean();
        this.ridingEntityId = buffer.getVarLong();
        if(this.mode == MODE_TELEPORT) {
            this.unknown1 = buffer.readLInt();
            this.unknown2 = buffer.readLInt();
        }
    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.putVarLong(this.entityId);
        buffer.writeVector3f(this.x, this.y, this.z);
        buffer.writeLFloat(this.yaw);
        buffer.writeLFloat(this.headYaw);
        buffer.writeLFloat(this.pitch);
        buffer.writeByte((byte) this.mode);
        buffer.writeBoolean(this.onGround);
        buffer.putVarLong(this.ridingEntityId);
        if(this.mode == MODE_TELEPORT) {
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

}
