package org.lunaris.material;

import org.lunaris.block.Block;
import org.lunaris.entity.Entity;

/**
 * Created by RINES on 24.09.17.
 */
public class ItemMaterial extends SpecifiedMaterial {

    protected ItemMaterial(Material material, String name) {
        super(material, name);
    }

    @Override
    public final boolean isBlock() {
        return false;
    }

    public int getEnchantAbility() {
        return 0;
    }

    public int getArmorPoints() {
        return 0;
    }

    public int getToughness() {
        return 0;
    }

    public int getMaxDurability() {
        return -1;
    }

    public boolean useOn(Block block, Entity user) {
        return false;
    }

    public boolean useOn(Entity entity, Entity user) {
        return false;
    }

}
