package org.lunaris.material.block;

import org.lunaris.api.world.Block;
import org.lunaris.api.item.ItemToolType;
import org.lunaris.api.material.Material;
import org.lunaris.util.math.AxisAlignedBB;

/**
 * Created by RINES on 24.09.17.
 */
public class BlockGrassPath extends BlockGrass {

    protected BlockGrassPath() {
        super(Material.GRASS_PATH, "Grass Path");
    }

    @Override
    public double getResistance() {
        return 3.25;
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

}
