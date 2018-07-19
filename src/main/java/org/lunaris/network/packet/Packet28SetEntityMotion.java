package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.entity.LEntity;
import org.lunaris.network.Packet;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet28SetEntityMotion extends Packet {

    private long entityId;
    private float motionX;
    private float motionY;
    private float motionZ;

    public Packet28SetEntityMotion() {}

    public Packet28SetEntityMotion(LEntity entity) {
        this(entity.getEntityID(), entity.getMotionX(), entity.getMotionY(), entity.getMotionZ());
    }

    public Packet28SetEntityMotion(long entityId, float motionX, float motionY, float motionZ) {
        this.entityId = entityId;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
    }

    @Override
    public byte getID() {
        return 0x28;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUnsignedVarLong(this.entityId);
        buffer.writeLFloat(this.motionX);
        buffer.writeLFloat(this.motionY);
        buffer.writeLFloat(this.motionZ);
    }

}
