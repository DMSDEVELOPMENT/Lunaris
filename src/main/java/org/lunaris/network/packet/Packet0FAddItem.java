package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;

import org.lunaris.api.item.ItemStack;
import org.lunaris.entity.Item;
import org.lunaris.entity.data.EntityMetadata;
import org.lunaris.network.Packet;
import org.lunaris.network.util.SerializationUtil;

/**
 * Created by RINES on 04.10.17.
 */
public class Packet0FAddItem extends Packet {

    private long entityID;
    private ItemStack itemStack;
    private float x, y, z;
    private float motionX, motionY, motionZ;
    private EntityMetadata metadata;
    private boolean isFromFishing = false;

    public Packet0FAddItem() {}

    public Packet0FAddItem(Item item) {
        this.entityID = item.getEntityID();
        this.itemStack = item.getItemStack();
        this.x = item.getX();
        this.y = item.getY();
        this.z = item.getZ();
        this.motionX = item.getMotionX();
        this.motionY = item.getMotionY();
        this.motionZ = item.getMotionZ();
        this.metadata = item.getDataProperties();
    }

    @Override
    public byte getID() {
        return 0x0f;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeSignedVarLong(this.entityID);
        buffer.writeUnsignedVarLong(this.entityID);
        SerializationUtil.writeItemStack(this.itemStack, buffer);
        buffer.writeLFloat(this.x);
        buffer.writeLFloat(this.y);
        buffer.writeLFloat(this.z);
        buffer.writeLFloat(this.motionX);
        buffer.writeLFloat(this.motionY);
        buffer.writeLFloat(this.motionZ);
        SerializationUtil.writeMetadata(this.metadata, buffer);
        buffer.writeBoolean(this.isFromFishing);
    }

}
