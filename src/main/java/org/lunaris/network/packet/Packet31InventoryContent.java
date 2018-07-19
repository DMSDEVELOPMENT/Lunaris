package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.api.item.ItemStack;
import org.lunaris.network.Packet;
import org.lunaris.network.util.SerializationUtil;

/**
 * Created by RINES on 01.10.17.
 */
public class Packet31InventoryContent extends Packet {

    private int inventoryId;
    private ItemStack[] items;

    public Packet31InventoryContent() {}

    public Packet31InventoryContent(int inventoryId, ItemStack[] items) {
        this.inventoryId = inventoryId;
        this.items = items;
    }

    @Override
    public byte getID() {
        return 0x31;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.inventoryId = buffer.readUnsignedVarInt();
        this.items = SerializationUtil.readItemStacks(buffer);
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUnsignedVarInt(this.inventoryId);
        SerializationUtil.writeItemStacks(this.items, buffer);
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

        public int getId() {
            return this.id;
        }

    }

}
