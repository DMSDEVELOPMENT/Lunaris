package org.lunaris.material.block;

import org.lunaris.block.Block;
import org.lunaris.block.BlockFace;
import org.lunaris.item.ItemStack;
import org.lunaris.item.ItemTier;
import org.lunaris.item.ItemToolType;
import org.lunaris.material.Material;

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
        if (hand != null && hand.isOfToolType(ItemToolType.PICKAXE) && hand.isOfTier(ItemTier.DIAMOND))
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
