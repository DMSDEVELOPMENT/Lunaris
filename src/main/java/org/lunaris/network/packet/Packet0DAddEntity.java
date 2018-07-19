package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;

import org.lunaris.api.entity.EntityType;
import org.lunaris.entity.LEntity;
import org.lunaris.entity.data.Attribute;
import org.lunaris.entity.data.EntityMetadata;
import org.lunaris.network.Packet;
import org.lunaris.network.util.SerializationUtil;

import java.util.Collection;

/**
 * Created by RINES on 04.10.17.
 */
public class Packet0DAddEntity extends Packet {

    private long entityID;
    private EntityType type;
    private float x, y, z;
    private float motionX, motionY, motionZ;
    private float yaw, headYaw, pitch;
    private Attribute[] attributes;
    private EntityMetadata metadata;

    public Packet0DAddEntity() {}

    public Packet0DAddEntity(LEntity entity) {
        this.entityID = entity.getEntityID();
        this.type = entity.getEntityType();
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        this.motionX = entity.getMotionX();
        this.motionY = entity.getMotionY();
        this.motionZ = entity.getMotionZ();
        this.yaw = entity.getYaw();
        this.headYaw = entity.getHeadYaw();
        this.pitch = entity.getPitch();
        Collection<Attribute> attributes = entity.getAttributes();
        this.attributes = new Attribute[attributes.size()];
        int i = 0;
        for (Attribute attribute : attributes)
            this.attributes[i] = attribute;
        this.metadata = entity.getDataProperties();
    }

    @Override
    public byte getID() {
        return 0x0d;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeSignedVarLong(this.entityID);
        buffer.writeUnsignedVarLong(this.entityID);
        buffer.writeUnsignedVarInt(this.type.getId());
        buffer.writeLFloat(this.x);
        buffer.writeLFloat(this.y);
        buffer.writeLFloat(this.z);
        buffer.writeLFloat(this.motionX);
        buffer.writeLFloat(this.motionY);
        buffer.writeLFloat(this.motionZ);
        buffer.writeLFloat(this.pitch);
        buffer.writeLFloat(this.yaw);
        buffer.writeLFloat(this.headYaw);
        buffer.writeUnsignedVarInt(this.attributes.length);
        for (Attribute attribute : this.attributes) {
            buffer.writeString(attribute.getName());
            buffer.writeLFloat(attribute.getMinValue());
            buffer.writeLFloat(attribute.getValue());
            buffer.writeLFloat(attribute.getMaxValue());
        }
        SerializationUtil.writeMetadata(this.metadata, buffer);
        buffer.writeUnsignedVarInt(0); //entity links
    }

}
