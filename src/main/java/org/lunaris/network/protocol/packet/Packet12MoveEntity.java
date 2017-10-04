package org.lunaris.network.protocol.packet;

import org.lunaris.entity.Entity;
import org.lunaris.entity.Player;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;
import org.lunaris.util.math.Vector3f;
import org.lunaris.world.Location;

/**
 * Created by RINES on 30.09.17.
 */
public class Packet12MoveEntity extends MinePacket {

    private long entityId;
    private float x, y, z;
    private float yaw, headYaw, pitch;
    private boolean onGround, teleport;

    public Packet12MoveEntity() {}

    public Packet12MoveEntity(Entity entity) {
        this.entityId = entity.getEntityID();
        this.x = entity.getX();
        this.y = entity.getY() + entity.getEyeHeight();
        this.z = entity.getZ();
        this.yaw = entity.getYaw();
        this.headYaw = entity.getHeadYaw();
        this.pitch = entity.getPitch();
        this.onGround = entity.isOnGround();
    }

    @Override
    public int getId() {
        return 0x12;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.entityId = buffer.readEntityRuntimeId();
        Vector3f position = buffer.readVector3f();
        this.x = position.x;
        this.y = position.y;
        this.z = position.z;
        this.pitch = (float) (buffer.readByte() * (360d / 256d));
        this.headYaw = (float) (buffer.readByte() * (360d / 256d));
        this.yaw = (float) (buffer.readByte() * (360d / 256d));
        this.onGround = buffer.readBoolean();
        this.teleport = buffer.readBoolean();
    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeEntityRuntimeId(this.entityId);
        buffer.writeVector3f(this.x, this.y, this.z);
        buffer.writeByte((byte) (this.pitch / (360d / 256d)));
        buffer.writeByte((byte) (this.headYaw / (360d / 256d)));
        buffer.writeByte((byte) (this.yaw / (360d / 256d)));
        buffer.writeBoolean(this.onGround);
        buffer.writeBoolean(this.teleport);
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

    public boolean isOnGround() {
        return onGround;
    }

    public boolean isTeleport() {
        return teleport;
    }

}
