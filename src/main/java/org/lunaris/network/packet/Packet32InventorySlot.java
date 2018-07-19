package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.api.item.ItemStack;
import org.lunaris.network.Packet;
import org.lunaris.network.util.SerializationUtil;

/**
 * Created by RINES on 01.10.17.
 */
public class Packet32InventorySlot extends Packet {

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
    public byte getID() {
        return 0x32;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.inventoryId = buffer.readUnsignedVarInt();
        this.slot = buffer.readUnsignedVarInt();
        this.item = SerializationUtil.readItemStack(buffer);
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUnsignedVarInt(this.inventoryId);
        buffer.writeUnsignedVarInt(this.slot);
        SerializationUtil.writeItemStack(this.item, buffer);
    }

}
