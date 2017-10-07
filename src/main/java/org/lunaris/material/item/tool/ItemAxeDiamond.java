package org.lunaris.material.item.tool;

import org.lunaris.item.ItemTier;
import org.lunaris.material.Material;

/**
 * Created by RINES on 07.10.17.
 */
public class ItemAxeDiamond extends ItemAbstractAxe {

    ItemAxeDiamond(Material type, String name) {
        super(type, name);
    }

    @Override
    public ItemTier getTier() {
        return ItemTier.DIAMOND;
    }

    @Override
    public int getAttackDamage() {
        return 6;
    }

}
