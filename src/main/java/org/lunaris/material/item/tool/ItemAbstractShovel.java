package org.lunaris.material.item.tool;

import org.lunaris.item.ItemTier;
import org.lunaris.item.ItemToolType;
import org.lunaris.material.ItemHandle;
import org.lunaris.material.Material;

/**
 * Created by RINES on 06.10.17.
 */
public abstract class ItemAbstractShovel extends ItemHandle {

    ItemAbstractShovel(Material type, String name) {
        super(type, name);
    }

    @Override
    public ItemToolType getToolType() {
        return ItemToolType.SHOVEL;
    }

    @Override
    public abstract ItemTier getTier();

    @Override
    public abstract  int getAttackDamage();

}
