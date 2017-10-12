package org.lunaris.api.material;

/**
 * Created by RINES on 12.10.17.
 */
public interface MaterialHandle {

    /**
     * Get this material's handle material.
     * @return this material's handle material.
     */
    Material getType();

    /**
     * Get this material's handle material id.
     * @return this material's handle material id.
     */
    int getTypeId();

    /**
     * Get this material's name in minecraft.
     * @param data data of the block/item.
     * @return this material's name in minecraft.
     */
    String getName(int data);

    /**
     * Whether this material handle is related to the block.
     * @return if this material handle is related to the block.
     */
    boolean isBlock();

    /**
     * Whether this material handle is related to an item (and not to the block).
     * @return if material handle is related to an item (and not to the block).
     */
    default boolean isItem() {
        return !isBlock();
    }

    /**
     * Get maximum size stack for this material handle for itemstack of given data.
     * @param data the data of itemstack.
     * @return maximum size stack for this material handle for itemstack of given data.
     */
    int getMaxStackSize(int data);

    /**
     * Get attack damage that would be dealt when player will attack somebody with item of this material handle in hand.
     * @return attack damage that would be dealt when player will attack somebody with item of this material handle in hand.
     */
    int getAttackDamage();

    /**
     * Get this handle as BlockHandle.
     * @return this handle as BlockHandle.
     */
    BlockHandle asBlock();

    /**
     * Get this handle as ItemHandle.
     * @return this handle as ItemHandle.
     */
    ItemHandle asItem();

}
