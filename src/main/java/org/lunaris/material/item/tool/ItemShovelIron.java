package org.lunaris.material.item.tool;

import org.lunaris.api.item.ItemTier;
import org.lunaris.api.material.Material;

/**
 * Created by RINES on 06.10.17.
 */
public class ItemShovelIron extends ItemAbstractShovel {

    ItemShovelIron(Material type, String name) {
        super(type, name);
    }

    @Override
    public ItemTier getTier() {
        return ItemTier.IRON;
    }

    @Override
    public int getAttackDamage() {
        return 3;
    }

}
