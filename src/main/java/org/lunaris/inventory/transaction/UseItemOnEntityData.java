package org.lunaris.inventory.transaction;

import org.lunaris.api.item.ItemStack;
import org.lunaris.network.packet.Packet1EInventoryTransaction;
import org.lunaris.util.math.Vector3f;

/**
 * Created by RINES on 01.10.17.
 */
public class UseItemOnEntityData implements TransactionData {

    private Packet1EInventoryTransaction.UseItemOnEntityActionType type;
    private long entityID;
    private int hotbarSlot;
    private ItemStack itemInHand;
    private Vector3f vector1, vector2;

    public UseItemOnEntityData() {}

    public UseItemOnEntityData(Packet1EInventoryTransaction.UseItemOnEntityActionType type, long entityID,
                               int hotbarSlot, ItemStack itemInHand, Vector3f vector1, Vector3f vector2) {
        this.type = type;
        this.entityID = entityID;
        this.hotbarSlot = hotbarSlot;
        this.itemInHand = itemInHand;
        this.vector1 = vector1;
        this.vector2 = vector2;
    }

    public Packet1EInventoryTransaction.UseItemOnEntityActionType getType() {
        return this.type;
    }

    public void setType(Packet1EInventoryTransaction.UseItemOnEntityActionType type) {
        this.type = type;
    }

    public long getEntityID() {
        return this.entityID;
    }

    public void setEntityID(long entityID) {
        this.entityID = entityID;
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

    public Vector3f getVector1() {
        return this.vector1;
    }

    public void setVector1(Vector3f vector1) {
        this.vector1 = vector1;
    }

    public Vector3f getVector2() {
        return this.vector2;
    }

    public void setVector2(Vector3f vector2) {
        this.vector2 = vector2;
    }

}
