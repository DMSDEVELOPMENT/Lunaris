package org.lunaris.material.block;

import org.lunaris.block.Block;
import org.lunaris.item.ItemStack;
import org.lunaris.item.ItemTier;
import org.lunaris.item.ItemToolType;
import org.lunaris.material.Material;

/**
 * Created by RINES on 24.09.17.
 */
public class BlockStone extends BlockSolid {

    protected BlockStone(Material material, String name) {
        super(material, name);
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
    public ItemStack[] getDrops(Block block, ItemStack hand) {
        if(hand.isOfToolType(ItemToolType.PICKAXE) && hand.isOfTier(ItemTier.WOODEN)) {
            ItemStack result;
            if(block.getData() == 0)
                result = new ItemStack(Material.COBBLESTONE, 1);
            else
                result = new ItemStack(Material.STONE, 1, block.getData());
            return new ItemStack[]{result};
        }
        return new ItemStack[0];
    }

    @Override
    public String getName(int data) {
        switch(data) {
            case 0: return "Stone";
            case 1: return "Granite";
            case 2: return "Polished Granite";
            case 3: return "Diorite";
            case 4: return "Polished Diorite";
            case 5: return "Andesite";
            case 6: return "Polished Andesite";
            default: return "Unknown Stone";
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
