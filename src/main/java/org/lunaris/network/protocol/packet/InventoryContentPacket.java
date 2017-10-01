package org.lunaris.network.protocol.packet;

import org.lunaris.item.ItemStack;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 01.10.17.
 */
public class InventoryContentPacket extends MinePacket {

    private InventoryContentType type;
    private ItemStack[] items;

    @Override
    public int getId() {
        return 0x31;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.type = InventoryContentType.values()[buffer.readUnsignedVarInt()];
        this.items = new ItemStack[buffer.readUnsignedVarInt()];
        for(int i = 0; i < this.items.length; ++i)
            this.items[i] = buffer.readItemStack();
    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeUnsignedVarInt(this.type.id);
        buffer.writeUnsignedVarInt(this.items.length);
        for(ItemStack item : this.items)
            buffer.writeItemStack(item);
    }

    public enum InventoryContentType {
        DEFAULT(0),
        OFFHAND(0x77),
        ARMOR(0x78),
        CREATIVE(0x79),
        HOTBAR(0x7a),
        FIXED_INVENTORY(0x7b);

        private final int id;

        InventoryContentType(int id) {
            this.id = id;
        }
    }

}
