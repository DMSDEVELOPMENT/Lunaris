package org.lunaris.material.block;

import org.lunaris.api.world.Block;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.item.ItemTier;
import org.lunaris.api.item.ItemToolType;
import org.lunaris.api.material.Material;

import java.util.Collections;
import java.util.List;

/**
 * Created by RINES on 24.09.17.
 */
public class BlockStone extends SolidBlock {

    protected BlockStone() {
        super(Material.STONE, "The Stone");
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 10;
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
        if (hand != null && hand.isOfToolTier(ItemToolType.PICKAXE, ItemTier.WOODEN)) {
            ItemStack result;
            if (block.getData() == 0)
                result = new ItemStack(Material.COBBLESTONE, 1);
            else
                result = new ItemStack(Material.STONE, 1, block.getData());
            return Collections.singletonList(result);
        }
        return Collections.emptyList();
    }

    @Override
    public String getName(int data) {
        switch (data) {
            case 0:
                return "Stone";
            case 1:
                return "Granite";
            case 2:
                return "Polished Granite";
            case 3:
                return "Diorite";
            case 4:
                return "Polished Diorite";
            case 5:
                return "Andesite";
            case 6:
                return "Polished Andesite";
            default:
                return "Unknown Stone";
        }
    }

    public enum Data {
        NORMAL,
        GRANITE,
        POLISHED_GRANITE,
        DIORITE,
        POLISHED_DIORITE,
        ANDESITE,
        POLISHED_ANDESITE
    }

}
