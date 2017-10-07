package org.lunaris.material.item.tool;

import org.lunaris.item.ItemTier;
import org.lunaris.material.Material;

/**
 * Created by RINES on 07.10.17.
 */
public class ItemPickaxeGold extends ItemAbstractPickaxe {

    ItemPickaxeGold(Material type, String name) {
        super(type, name);
    }

    @Override
    public ItemTier getTier() {
        return ItemTier.GOLD;
    }

    @Override
    public int getAttackDamage() {
        return 2;
    }

}
