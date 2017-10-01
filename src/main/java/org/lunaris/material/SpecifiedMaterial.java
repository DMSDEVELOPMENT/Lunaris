package org.lunaris.material;

import org.lunaris.item.ItemTier;
import org.lunaris.item.ItemToolType;

/**
 * Created by RINES on 13.09.17.
 */
public abstract class SpecifiedMaterial {

    private final Material material;
    private final String name;

    protected SpecifiedMaterial(Material material, String name) {
        if (material == null)
            throw new IllegalArgumentException("Pizda");
        this.material = material;
        this.name = name;
    }

    public Material getType() {
        return this.material;
    }

    public int getTypeId() {
        return this.material.getId();
    }

    public String getName(int data) {
        return this.name;
    }

    public abstract boolean isBlock();

    public int getMaxStackSize() {
        return 64;
    }

    public int getAttackDamage() {
        return 1;
    }

    public ItemToolType getToolType() {
        return ItemToolType.NONE;
    }

    public ItemTier getTier() {
        return ItemTier.NONE;
    }

}
