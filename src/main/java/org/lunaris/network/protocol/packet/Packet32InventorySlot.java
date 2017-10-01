package org.lunaris.network.protocol.packet;

import org.lunaris.item.ItemStack;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 01.10.17.
 */
public class Packet32InventorySlot extends MinePacket {

    private int inventoryId;
    private int slot;
    private ItemStack item;

    public Packet32InventorySlot() {}

    public Packet32InventorySlot(int inventoryId, int slot, ItemStack item) {
        this.inventoryId = inventoryId;
        this.slot = slot;
        this.item = item;
    }

    @Override
    public int getId() {
        return 0x32;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.inventoryId = buffer.readUnsignedVarInt();
        this.slot = buffer.readUnsignedVarInt();
        this.item = buffer.readItemStack();
    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeUnsignedVarInt(this.inventoryId);
        buffer.writeUnsignedVarInt(this.slot);
        buffer.writeItemStack(this.item);
    }

}
