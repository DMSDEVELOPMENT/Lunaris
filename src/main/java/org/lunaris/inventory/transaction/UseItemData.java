package org.lunaris.inventory.transaction;

import org.lunaris.api.item.ItemStack;
import org.lunaris.api.world.BlockFace;
import org.lunaris.network.packet.Packet1EInventoryTransaction;
import org.lunaris.util.math.Vector3f;
import org.lunaris.world.BlockVector;

/**
 * Created by RINES on 01.10.17.
 */
public class UseItemData implements TransactionData {

    private Packet1EInventoryTransaction.UseItemActionType type;
    private BlockVector blockPosition;
    private BlockFace blockFace;
    private int hotbarSlot;
    private ItemStack itemInHand;
    private Vector3f playerPosition;
    private Vector3f clickPosition;

    public UseItemData() {}

    public UseItemData(Packet1EInventoryTransaction.UseItemActionType type, BlockVector blockPosition,
                       BlockFace blockFace, int hotbarSlot, ItemStack itemInHand, Vector3f playerPosition, Vector3f clickPosition) {
        this.type = type;
        this.blockPosition = blockPosition;
        this.blockFace = blockFace;
        this.hotbarSlot = hotbarSlot;
        this.itemInHand = itemInHand;
        this.playerPosition = playerPosition;
        this.clickPosition = clickPosition;
    }

    public Packet1EInventoryTransaction.UseItemActionType getType() {
        return type;
    }

    public void setType(Packet1EInventoryTransaction.UseItemActionType type) {
        this.type = type;
    }

    public BlockVector getBlockPosition() {
        return this.blockPosition;
    }

    public void setBlockPosition(BlockVector blockPosition) {
        this.blockPosition = blockPosition;
    }

    public BlockFace getBlockFace() {
        return this.blockFace;
    }

    public void setBlockFace(BlockFace blockFace) {
        this.blockFace = blockFace;
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

    public void setPlayerPosition(Vector3f playerPosition) {
        this.playerPosition = playerPosition;
    }

    public Vector3f getClickPosition() {
        return this.clickPosition;
    }

    public void setClickPosition(Vector3f clickPosition) {
        this.clickPosition = clickPosition;
    }
}
