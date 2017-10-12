package org.lunaris.material.block;

import org.lunaris.api.world.Block;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.item.ItemTier;
import org.lunaris.api.item.ItemToolType;
import org.lunaris.api.material.Material;

import java.util.Collections;
import java.util.List;

/**
 * Created by RINES on 07.10.17.
 */
public class BlockIronOre extends SolidBlock {

    protected BlockIronOre() {
        super(Material.IRON_ORE, "iron_ore");
    }

    @Override
    public ItemToolType getRequiredToolType() {
        return ItemToolType.PICKAXE;
    }

    @Override
    public List<ItemStack> getDrops(Block block, ItemStack hand) {
        if(hand != null && hand.isOfToolType(ItemToolType.PICKAXE) && hand.isOfTier(ItemTier.STONE))
            return Collections.singletonList(new ItemStack(Material.IRON_ORE));
        return Collections.emptyList();
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
