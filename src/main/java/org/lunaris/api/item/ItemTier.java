package org.lunaris.api.item;

/**
 * Created by RINES on 24.09.17.
 */
public enum ItemTier {
    NONE(-1),
    WOODEN(60),
    GOLD(33),
    STONE(132),
    IRON(251),
    DIAMOND(1562);

    private final int maxDurability;

    ItemTier(int maxDurability) {
        this.maxDurability = maxDurability;
    }

    /**
     * Get max durability of items of that tier.
     *
     * @return max durability of items of that tier.
     */
    public int getMaxDurability() {
        return this.maxDurability;
    }

}
