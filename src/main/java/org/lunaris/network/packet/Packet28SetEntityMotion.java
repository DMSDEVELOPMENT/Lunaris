package org.lunaris.network.packet;

import org.lunaris.entity.LEntity;
import org.lunaris.network_old.protocol.MineBuffer;
import org.lunaris.network_old.protocol.MinePacket;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet28SetEntityMotion extends MinePacket {

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
    public int getId() {
        return 0x28;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeVarLong(this.entityId);
        buffer.writeVector3f(this.motionX, this.motionY, this.motionZ);
    }

}
