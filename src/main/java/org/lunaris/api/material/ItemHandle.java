package org.lunaris.api.material;

import org.lunaris.api.item.ItemTier;
import org.lunaris.api.item.ItemToolType;

/**
 * Created by RINES on 12.10.17.
 */
public interface ItemHandle extends MaterialHandle {

    /**
     * Get this item handle's enchant ability.
     *
     * @return this item handle's enchant ability.
     */
    int getEnchantAbility();

    /**
     * Get this item handle's armor points.
     *
     * @return this item handle's armor points.
     */
    int getArmorPoints();

    /**
     * Get this item handle's toughness.
     *
     * @return this item handle's toughness.
     */
    int getToughness();

    /**
     * Get this item handle's max durability.
     *
     * @return -1, whether this item handle doesn't have any durability at all; some number otherwise.
     */
    int getMaxDurability();

    /**
     * Check whether items of this material handle can be used by right clicking with them.
     *
     * @return if items of this material handle can be used by right clicking with them.
     */
    boolean canBeUsed();

    /**
     * Get items of this material handle tool type.
     *
     * @return tool type of items of this material handle.
     */
    ItemToolType getToolType();

    /**
     * Get items of this material handle tier.
     *
     * @return tier of items of this material handle.
     */
    ItemTier getTier();

}
