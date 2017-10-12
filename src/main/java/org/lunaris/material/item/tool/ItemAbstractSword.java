package org.lunaris.material.item.tool;

import org.lunaris.api.item.ItemTier;
import org.lunaris.api.item.ItemToolType;
import org.lunaris.material.LItemHandle;
import org.lunaris.api.material.Material;

/**
 * Created by RINES on 06.10.17.
 */
public abstract class ItemAbstractSword extends LItemHandle {

    ItemAbstractSword(Material type, String name) {
        super(type, name);
    }

    @Override
    public ItemToolType getToolType() {
        return ItemToolType.SWORD;
    }

    @Override
    public abstract ItemTier getTier();

    @Override
    public abstract  int getAttackDamage();

}
