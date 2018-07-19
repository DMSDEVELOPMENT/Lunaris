package org.lunaris.network.packet;

import org.lunaris.entity.Item;
import org.lunaris.entity.data.EntityMetadata;
import org.lunaris.api.item.ItemStack;
import org.lunaris.network_old.protocol.MineBuffer;
import org.lunaris.network_old.protocol.MinePacket;

/**
 * Created by RINES on 04.10.17.
 */
public class Packet0FAddItem extends MinePacket {

    private long entityID;
    private ItemStack itemStack;
    private float x, y, z;
    private float motionX, motionY, motionZ;
    private EntityMetadata metadata;

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
    public int getId() {
        return 0x0f;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeEntityUniqueId(this.entityID);
        buffer.writeEntityRuntimeId(this.entityID);
        buffer.writeItemStack(this.itemStack);
        buffer.writeVector3f(this.x, this.y, this.z);
        buffer.writeVector3f(this.motionX, this.motionY, this.motionZ);
        buffer.writeMetadata(this.metadata);
    }

}
