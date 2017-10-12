package org.lunaris.material.block;

import org.lunaris.api.world.Block;
import org.lunaris.block.BlockColor;
import org.lunaris.item.ItemStack;
import org.lunaris.item.ItemToolType;
import org.lunaris.material.Material;

import java.util.Collections;
import java.util.List;

/**
 * @author xtrafrancyz
 */
public class BlockPlanks extends SolidBlock {
    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;
    public static final int ACACIA = 4;
    public static final int DARK_OAK = 5;

    protected BlockPlanks() {
        super(Material.PLANKS, null);
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public ItemToolType getRequiredToolType() {
        return ItemToolType.AXE;
    }

    @Override
    public String getName(int data) {
        return new String[]{
            "Oak Wood Planks",
            "Spruce Wood Planks",
            "Birch Wood Planks",
            "Jungle Wood Planks",
            "Acacia Wood Planks",
            "Dark Oak Wood Planks"
        }[data];
    }

    @Override
    public BlockColor getColor(int data) {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public List<ItemStack> getDrops(Block block, ItemStack hand) {
        return Collections.singletonList(new ItemStack(Material.PLANKS, 1, block.getData()));
    }

}
