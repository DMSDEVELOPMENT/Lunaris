package org.lunaris.inventory.transaction;

import org.lunaris.api.item.ItemStack;
import org.lunaris.network.packet.Packet1EInventoryTransaction;
import org.lunaris.api.util.math.Vector3d;

/**
 * Created by RINES on 01.10.17.
 */
public class ReleaseItemData implements TransactionData {

    private Packet1EInventoryTransaction.ReleaseItemActionType type;
    private int hotbarSlot;
    private ItemStack itemInHand;
    private Vector3d headRotation;

    public ReleaseItemData() {}

    public ReleaseItemData(Packet1EInventoryTransaction.ReleaseItemActionType type, int hotbarSlot, ItemStack itemInHand, Vector3d headRotation) {
        this.type = type;
        this.hotbarSlot = hotbarSlot;
        this.itemInHand = itemInHand;
        this.headRotation = headRotation;
    }

    public Packet1EInventoryTransaction.ReleaseItemActionType getType() {
        return this.type;
    }

    public void setType(Packet1EInventoryTransaction.ReleaseItemActionType type) {
        this.type = type;
    }

    public int getHotbarSlot() {
        return this.hotbarSlot;
    }

    public void setHotbarSlot(int hotbarSlot) {
        this.hotbarSlot = hotbarSlot;
    }

    public ItemStack getItemInHand() {
        return this.itemInHand;
    }

    public void setItemInHand(ItemStack itemInHand) {
        this.itemInHand = itemInHand;
    }

    public Vector3d getHeadRotation() {
        return this.headRotation;
    }

    public void setHeadRotation(Vector3d headRotation) {
        this.headRotation = headRotation;
    }

}
