package org.lunaris.api.material;

import org.lunaris.api.item.ItemToolType;
import org.lunaris.api.world.Block;
import org.lunaris.api.item.ItemStack;

import java.util.List;

/**
 * Created by RINES on 12.10.17.
 */
public interface BlockHandle extends MaterialHandle {

    /**
     * Get this block handle's hardness.
     * Used in breaking time calculations.
     * @return this block handle's hardness.
     */
    double getHardness();

    /**
     * Get this block handle's resistance.
     * @return this block handle's resistance.
     */
    double getResistance();

    /**
     * Get this block handle's friction factor.
     * The more it is, the more entities will "skate", running on blocks of this material handle.
     * @return this block handle' friction factor.
     */
    double getFrictionFactor();

    /**
     * Get this block handle's light level.
     * @return this block handle's light level
     */
    int getLightLevel();

    /**
     * Check whether blocks of this material handle can be placed.
     * @return if blocks of this material handle can be placed.
     */
    boolean canBePlaced();

    /**
     * Check whether blocks of this material handle are transparent.
     * @return if blocks of this material handle are transparent.
     */
    boolean isTransparent();

    /**
     * Check whether blocks of this material handle are solid.
     * @return if blocks of this material handle are solid.
     */
    boolean isSolid();

    /**
     * Check whether entities can pass through blocks of this material handle.
     * @return if entities can pass through blocks of this material handle.
     */
    boolean canPassThrough();

    /**
     * Get drops of block of this material handle, when player breaks it with given item in hand.
     * @param block the block of this material handle.
     * @param handItem the item in player's hand.
     * @return list of drops.
     */
    List<ItemStack> getDrops(Block block, ItemStack handItem);

    /**
     * Get tool type of items, required to break blocks of this material handle.
     * For example, pickaxe is required for stone/ores/obsidian; shovel is required for dirt.
     * @return tool type of items, required to break blocks of this material handle.
     */
    ItemToolType getRequiredToolType();

}
