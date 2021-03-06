package org.lunaris.inventory.transaction;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.api.item.ItemStack;
import org.lunaris.entity.LPlayer;
import org.lunaris.inventory.LInventory;
import org.lunaris.inventory.transaction.action.CreativeInventoryAction;
import org.lunaris.inventory.transaction.action.DropItemAction;
import org.lunaris.inventory.transaction.action.SlotChangeAction;
import org.lunaris.network.packet.Packet1EInventoryTransaction;
import org.lunaris.network.util.SerializationUtil;

/**
 * Created by RINES on 01.10.17.
 */
public class InventoryActionData {

    private InventoryActionSource source;
    private int windowId;
    private int unknown;
    private int inventorySlot;
    private ItemStack oldItem, newItem;

    public InventoryActionData() {
    }

    public InventoryActionData(PacketBuffer buffer) {
        int id = buffer.readUnsignedVarInt();
        for (InventoryActionSource source : InventoryActionSource.values())
            if (source.getId() == id) {
                this.source = source;
                break;
            }
        switch (this.source) {
            case CONTAINER:
                this.windowId = buffer.readSignedVarInt();
                break;
            case WORLD:
                this.unknown = buffer.readUnsignedVarInt();
                break;
            case CREATIVE:
                break;
            case TODO:
                this.windowId = buffer.readSignedVarInt();
                break;
        }
        this.inventorySlot = buffer.readUnsignedVarInt();
        this.oldItem = SerializationUtil.readItemStack(buffer);
        this.newItem = SerializationUtil.readItemStack(buffer);
    }

    public void write(PacketBuffer buffer) {
        buffer.writeUnsignedVarInt(this.source.getId());
        switch (this.source) {
            case CONTAINER:
                buffer.writeSignedVarInt(this.windowId);
                break;
            case WORLD:
                buffer.writeUnsignedVarInt(this.unknown);
                break;
            case CREATIVE:
                break;
            case TODO:
                buffer.writeSignedVarInt(this.windowId);
                break;
        }
        buffer.writeUnsignedVarInt(this.inventorySlot);
        SerializationUtil.writeItemStack(this.oldItem, buffer);
        SerializationUtil.writeItemStack(this.newItem, buffer);
    }

    public InventoryAction toInventoryAction(LPlayer player) {
        switch (this.source) {
            case CONTAINER: {
                if (this.windowId == InventorySection.ARMOR.getId()) {
                    this.inventorySlot += 36;
                    this.windowId = InventorySection.INVENTORY.getId();
                }
                LInventory inventory = player.getInventoryManager().getInventoryById(this.windowId);
                if (inventory != null)
                    return new SlotChangeAction(inventory, this.inventorySlot, this.oldItem, this.newItem);
                throw new RuntimeException("Player " + player.getName() + " has no open container with window ID " + this.windowId);
            }
            case WORLD: {
                if (this.inventorySlot != Packet1EInventoryTransaction.MagicActionType.DROP_ITEM.ordinal())
                    break;
                return new DropItemAction(this.oldItem, this.newItem);
            }
            case CREATIVE: {
                Packet1EInventoryTransaction.CreativeMagicActionType type = Packet1EInventoryTransaction.CreativeMagicActionType.values()[this.inventorySlot];
                return new CreativeInventoryAction(type, this.oldItem, this.newItem);
            }
            case TODO: {
                //Soon
                break;
            }
        }
        return null;
    }

}
