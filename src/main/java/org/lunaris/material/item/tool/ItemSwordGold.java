package org.lunaris.material.item.tool;

import org.lunaris.api.item.ItemTier;
import org.lunaris.api.material.Material;

/**
 * Created by RINES on 07.10.17.
 */
public class ItemSwordGold extends ItemAbstractSword {

    ItemSwordGold(Material type, String name) {
        super(type, name);
    }

    @Override
    public ItemTier getTier() {
        return ItemTier.GOLD;
    }

    @Override
    public int getAttackDamage() {
        return 4;
    }

}
