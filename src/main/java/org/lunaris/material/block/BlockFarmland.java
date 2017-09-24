package org.lunaris.material.block;

import org.lunaris.block.Block;
import org.lunaris.block.BlockColor;
import org.lunaris.item.ItemStack;
import org.lunaris.item.ItemToolType;
import org.lunaris.material.Material;
import org.lunaris.util.math.AxisAlignedBB;

/**
 * Created by RINES on 24.09.17.
 */
public class BlockFarmland extends BlockTransparent {

    protected BlockFarmland() {
        super(Material.FARM_LAND, "Farmland");
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public ItemToolType getRequiredToolType() {
        return ItemToolType.SHOVEL;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox(Block block) {
        return new AxisAlignedBB(
                block.getX(),
                block.getY(),
                block.getZ(),
                block.getX() + 1,
                block.getY() + .9375,
                block.getZ() + 1
        );
    }

    @Override
    public ItemStack[] getDrops(Block block, ItemStack hand) {
        return new ItemStack[]{new ItemStack(Material.DIRT, 1)};
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }

}
