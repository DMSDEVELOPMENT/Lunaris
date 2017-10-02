package org.lunaris.material.block;

import org.lunaris.block.Block;
import org.lunaris.item.ItemStack;
import org.lunaris.item.ItemTier;
import org.lunaris.item.ItemToolType;
import org.lunaris.material.Material;

import java.util.Collections;
import java.util.List;

/**
 * Created by RINES on 24.09.17.
 */
public class BlockCobblestone extends SolidBlock {

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
    public List<ItemStack> getDrops(Block block, ItemStack hand) {
        if (hand != null && hand.isOfToolTier(ItemToolType.PICKAXE, ItemTier.WOODEN))
            return Collections.singletonList(new ItemStack(Material.COBBLESTONE, 1));
        return Collections.emptyList();
    }

}
