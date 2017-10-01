package org.lunaris.material.block;

import org.lunaris.block.Block;
import org.lunaris.material.Material;
import org.lunaris.world.Dimension;

/**
 * @author xtrafrancyz
 */
public class BlockLava extends LiquidDynamicBlock {
    protected BlockLava() {
        super(Material.LAVA, "Lava");
    }

    @Override
    public int tickRate(Block block) {
        return block.getWorld().getDimension() == Dimension.THE_NETHER ? 5 : 30;
    }

}
