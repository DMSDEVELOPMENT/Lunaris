package org.lunaris.material.item.tool;

import org.lunaris.api.item.ItemTier;
import org.lunaris.api.item.ItemToolType;
import org.lunaris.api.material.Material;
import org.lunaris.material.LItemHandle;

/**
 * Created by RINES on 06.10.17.
 */
public abstract class ItemAbstractPickaxe extends LItemHandle {

    ItemAbstractPickaxe(Material type, String name) {
        super(type, name);
    }

    @Override
    public ItemToolType getToolType() {
        return ItemToolType.PICKAXE;
    }

    @Override
    public abstract ItemTier getTier();

    @Override
    public abstract int getAttackDamage();

}
