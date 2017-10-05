package org.lunaris.material;

import org.lunaris.block.Block;
import org.lunaris.block.BlockFace;
import org.lunaris.entity.Entity;
import org.lunaris.entity.Player;
import org.lunaris.item.ItemStack;
import org.lunaris.item.ItemTier;
import org.lunaris.item.ItemToolType;

/**
 * Created by RINES on 24.09.17.
 */
public class ItemHandle extends MaterialHandle {

    protected ItemHandle(Material type, String name) {
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
