package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;

import org.lunaris.api.item.ItemStack;
import org.lunaris.network.Packet;
import org.lunaris.network.util.SerializationUtil;

/**
 * Created by RINES on 01.10.17.
 */
public class Packet1FMobEquipment extends Packet {

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
    public byte getID() {
        return 0x1f;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.entityID = buffer.readUnsignedVarLong();
        this.item = SerializationUtil.readItemStack(buffer);
        this.inventorySlot = buffer.readByte();
        this.hotbarSlot = buffer.readByte();
        this.inventoryId = buffer.readByte();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUnsignedVarLong(this.entityID);
        SerializationUtil.writeItemStack(this.item, buffer);
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
