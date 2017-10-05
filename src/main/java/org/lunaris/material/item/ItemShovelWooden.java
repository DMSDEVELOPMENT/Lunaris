package org.lunaris.material.item;

import org.lunaris.item.ItemTier;
import org.lunaris.material.Material;

/**
 * Created by RINES on 06.10.17.
 */
public class ItemShovelWooden extends ItemAbstractShovel {

    public ItemShovelWooden(Material type, String name) {
        super(type, name);
    }

    @Override
    public ItemTier getTier() {
        return ItemTier.WOODEN;
    }

    @Override
    public int getAttackDamage() {
        return 1;
    }

}
