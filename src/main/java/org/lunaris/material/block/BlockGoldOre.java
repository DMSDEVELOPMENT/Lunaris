package org.lunaris.material.block;

import org.lunaris.api.item.ItemStack;
import org.lunaris.api.item.ItemTier;
import org.lunaris.api.item.ItemToolType;
import org.lunaris.api.material.Material;
import org.lunaris.api.world.Block;
import org.lunaris.block.BlockColor;

import java.util.Collections;
import java.util.List;

/**
 * Created by RINES on 07.10.17.
 */
public class BlockGoldOre extends SolidBlock {

    protected BlockGoldOre() {
        super(Material.GOLD_ORE, "gold_ore");
    }

    @Override
    public ItemToolType getRequiredToolType() {
        return ItemToolType.PICKAXE;
    }

    @Override
    public List<ItemStack> getDrops(Block block, ItemStack hand) {
        if(hand != null && hand.isOfToolType(ItemToolType.PICKAXE) && hand.isOfTier(ItemTier.IRON))
            return Collections.singletonList(new ItemStack(Material.GOLD_ORE));
        return Collections.emptyList();
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public BlockColor getColor(int data) {
        return BlockColor.GOLD_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
