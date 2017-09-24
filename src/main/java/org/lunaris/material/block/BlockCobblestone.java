package org.lunaris.material.block;

import org.lunaris.block.Block;
import org.lunaris.item.ItemStack;
import org.lunaris.item.ItemTier;
import org.lunaris.item.ItemToolType;
import org.lunaris.material.Material;

/**
 * Created by RINES on 24.09.17.
 */
public class BlockCobblestone extends BlockSolid {

    protected BlockCobblestone() {
        super(Material.COBBLESTONE, "Cobblestone");
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public ItemToolType getRequiredToolType() {
        return ItemToolType.PICKAXE;
    }

    @Override
    public ItemStack[] getDrops(Block block, ItemStack hand) {
        if(hand.isOfToolType(ItemToolType.PICKAXE) && hand.isOfTier(ItemTier.WOODEN))
            return new ItemStack[]{new ItemStack(Material.COBBLESTONE, 1)};
        return new ItemStack[0];
    }

}
