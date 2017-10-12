package org.lunaris.network.protocol.packet;

import org.lunaris.api.item.ItemStack;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 01.10.17.
 */
public class Packet1FMobEquipment extends MinePacket {

    private long entityID;
    private ItemStack item;
    private int inventorySlot, hotbarSlot, inventoryId;

    public Packet1FMobEquipment() {}

    public Packet1FMobEquipment(long entityID, ItemStack item, int inventorySlot, int hotbarSlot, int inventoryId) {
        this.entityID = entityID;
        this.item = item;
        this.inventorySlot = inventorySlot;
        this.hotbarSlot = hotbarSlot;
        this.inventoryId = inventoryId;
    }

    @Override
    public int getId() {
        return 0x1f;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.entityID = buffer.readEntityRuntimeId();
        this.item = buffer.readItemStack();
        this.inventorySlot = buffer.readByte();
        this.hotbarSlot = buffer.readByte();
        this.inventoryId = buffer.readByte();
    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeEntityRuntimeId(this.entityID);
        buffer.writeItemStack(this.item);
        buffer.writeByte((byte) this.inventorySlot);
        buffer.writeByte((byte) this.hotbarSlot);
        buffer.writeByte((byte) this.inventoryId);
    }

    public long getEntityID() {
        return entityID;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public int getInventorySlot() {
        return this.inventorySlot;
    }

    public int getHotbarSlot() {
        return this.hotbarSlot;
    }

    public int getInventoryId() {
        return this.inventoryId;
    }
}
