package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.inventory.transaction.InventorySection;
import org.lunaris.network.Packet;

/**
 * Created by RINES on 02.10.17.
 */
public class Packet30PlayerHotbar extends Packet {

    private int activeSlot;
    private int inventoryId = InventorySection.INVENTORY.getId();
    private int[] slots;
    private boolean selectHotbarSlot = true;

    @Override
    public byte getID() {
        return 0x30;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.activeSlot = buffer.readUnsignedVarInt();
        this.inventoryId = buffer.readByte();
//        this.slots = new int[buffer.readUnsignedVarInt()];
//        for(int i = 0; i < this.slots.length; ++i)
//            this.slots[i] = signInt(buffer.readUnsignedVarInt());
        this.selectHotbarSlot = buffer.readBoolean();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUnsignedVarInt(this.activeSlot);
        buffer.writeByte((byte) this.inventoryId);
//        buffer.writeUnsignedVarInt(this.slots.length);
//        for(int i : slots)
//            buffer.writeUnsignedVarInt(i);
        buffer.writeBoolean(this.selectHotbarSlot);
    }

    public int getActiveSlot() {
        return this.activeSlot;
    }

    public int getInventoryId() {
        return this.inventoryId;
    }

    public int[] getSlots() {
        return this.slots;
    }

    public boolean isSelectHotbarSlot() {
        return this.selectHotbarSlot;
    }

    private int signInt(int value) {
        return value << 32 >> 32;
    }

}
