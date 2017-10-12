package org.lunaris.inventory.transaction;

import org.lunaris.api.world.BlockFace;
import org.lunaris.item.ItemStack;
import org.lunaris.network.protocol.packet.Packet1EInventoryTransaction;
import org.lunaris.api.util.math.Vector3d;
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
    private Vector3d playerPosition;
    private Vector3d clickPosition;

    public UseItemData() {}

    public UseItemData(Packet1EInventoryTransaction.UseItemActionType type, BlockVector blockPosition,
                       BlockFace blockFace, int hotbarSlot, ItemStack itemInHand, Vector3d playerPosition, Vector3d clickPosition) {
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

    public Vector3d getPlayerPosition() {
        return this.playerPosition;
    }

    public void setPlayerPosition(Vector3d playerPosition) {
        this.playerPosition = playerPosition;
    }

    public Vector3d getClickPosition() {
        return this.clickPosition;
    }

    public void setClickPosition(Vector3d clickPosition) {
        this.clickPosition = clickPosition;
    }
}
