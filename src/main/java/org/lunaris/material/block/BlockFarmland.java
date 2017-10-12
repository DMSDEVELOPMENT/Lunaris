package org.lunaris.material.block;

import org.lunaris.api.world.Block;
import org.lunaris.block.BlockColor;
import org.lunaris.item.ItemStack;
import org.lunaris.item.ItemToolType;
import org.lunaris.material.Material;
import org.lunaris.util.math.AxisAlignedBB;

import java.util.Collections;
import java.util.List;

/**
 * Created by RINES on 24.09.17.
 */
public class BlockFarmland extends TransparentBlock {

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
    public AxisAlignedBB recalculateBoundingBox(Block block) {
        return new AxisAlignedBB(
            block.getX(),
            block.getY(),
            block.getZ(),
            block.getX() + 1,
            block.getY() + .9375F,
            block.getZ() + 1
        );
    }

    @Override
    public List<ItemStack> getDrops(Block block, ItemStack hand) {
        return Collections.singletonList(new ItemStack(Material.DIRT, 1));
    }

    @Override
    public BlockColor getColor(int data) {
        return BlockColor.DIRT_BLOCK_COLOR;
    }

}
