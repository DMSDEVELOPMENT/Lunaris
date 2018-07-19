package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.entity.LEntity;
import org.lunaris.network.Packet;
import org.lunaris.network.util.SerializationUtil;
import org.lunaris.util.math.Vector3f;

/**
 * Created by RINES on 30.09.17.
 */
public class Packet12MoveEntity extends Packet {

    private static final byte FLAG_ON_GROUND = 0x1;
    private static final byte FLAG_TELEPORTED = 0x2;

    private long entityId;
    private float x, y, z;
    private float yaw, headYaw, pitch;
    private boolean onGround, teleported;

    public Packet12MoveEntity() {}

    public Packet12MoveEntity(LEntity entity) {
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
    public byte getID() {
        return 0x12;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUnsignedVarLong(this.entityId);
        byte flags = this.onGround ? FLAG_ON_GROUND : 0;
        if (this.teleported) {
            flags |= FLAG_TELEPORTED;
        }
        buffer.writeByte(flags);
        SerializationUtil.writeVector3f(new Vector3f(this.x, this.y, this.z), buffer);
        SerializationUtil.writeByteRotation(this.pitch, buffer);
        SerializationUtil.writeByteRotation(this.headYaw, buffer);
        SerializationUtil.writeByteRotation(this.yaw, buffer);
    }

    public long getEntityId() {
        return this.entityId;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getHeadYaw() {
        return this.headYaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public boolean isTeleport() {
        return this.teleported;
    }

}
