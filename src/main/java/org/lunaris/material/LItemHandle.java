package org.lunaris.material;

import org.lunaris.api.entity.Entity;
import org.lunaris.api.entity.Player;
import org.lunaris.api.material.ItemHandle;
import org.lunaris.api.material.Material;
import org.lunaris.api.world.Block;
import org.lunaris.api.world.BlockFace;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.item.ItemTier;
import org.lunaris.api.item.ItemToolType;

/**
 * Created by RINES on 24.09.17.
 */
public class LItemHandle extends LMaterialHandle implements ItemHandle {

    protected LItemHandle(Material type, String name) {
        super(type, name);
    }

    @Override
    public final boolean isBlock() {
        return false;
    }

    public ItemToolType getToolType() {
        return ItemToolType.NONE;
    }

    public ItemTier getTier() {
        return ItemTier.NONE;
    }

    public boolean useOn(ItemStack item, Block block, BlockFace face, Player player) {
        return false;
    }

    public boolean useOn(Entity entity, ItemStack item, Entity user) {
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
        return getTier().getMaxDurability();
    }

    public boolean canBeUsed() {
        return false;
    }

}
