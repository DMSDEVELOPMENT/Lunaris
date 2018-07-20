package org.lunaris.inventory.transaction;

import org.lunaris.api.item.ItemStack;
import org.lunaris.network.packet.Packet1EInventoryTransaction;
import org.lunaris.util.math.Vector3f;

/**
 * Created by RINES on 01.10.17.
 */
public class ReleaseItemData implements TransactionData {

    private Packet1EInventoryTransaction.ReleaseItemActionType type;
    private int hotbarSlot;
    private ItemStack itemInHand;
    private Vector3f playerPosition;

    public ReleaseItemData() {
    }

    public ReleaseItemData(Packet1EInventoryTransaction.ReleaseItemActionType type, int hotbarSlot, ItemStack itemInHand, Vector3f playerPosition) {
        this.type = type;
        this.hotbarSlot = hotbarSlot;
        this.itemInHand = itemInHand;
        this.playerPosition = playerPosition;
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

    public Vector3f getPlayerPosition() {
        return this.playerPosition;
    }

    public void setPlayerPosition(Vector3f headRotation) {
        this.playerPosition = headRotation;
    }

}
