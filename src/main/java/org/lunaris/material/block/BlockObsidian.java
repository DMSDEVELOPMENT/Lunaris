package org.lunaris.material.block;

import org.lunaris.api.world.Block;
import org.lunaris.api.world.BlockFace;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.item.ItemTier;
import org.lunaris.api.item.ItemToolType;
import org.lunaris.api.material.Material;

import java.util.Collections;
import java.util.List;

/**
 * @author xtrafrancyz
 */
public class BlockObsidian extends SolidBlock {

    protected BlockObsidian() {
        super(Material.OBSIDIAN, "Obsidian");
    }

    @Override
    public ItemToolType getRequiredToolType() {
        return ItemToolType.PICKAXE;
    }

    @Override
    public double getHardness() {
        return 50;
    }

    @Override
    public double getResistance() {
        return 6000;
    }

    @Override
    public List<ItemStack> getDrops(Block block, ItemStack hand) {
        if (hand != null && hand.isOfToolTier(ItemToolType.PICKAXE, ItemTier.DIAMOND))
            return Collections.singletonList(new ItemStack(getType()));
        return Collections.emptyList();
    }

    @Override
    public boolean onBreak(ItemStack item, Block block) {
        for (BlockFace face : BlockFace.values())
            if (block.getSide(face).getType() == Material.NETHER_PORTAL)
                block.setType(Material.AIR);
        return super.onBreak(item, block);
    }

}
