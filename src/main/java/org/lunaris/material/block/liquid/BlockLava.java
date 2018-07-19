package org.lunaris.material.block.liquid;

import org.lunaris.api.material.Material;
import org.lunaris.api.world.Block;
import org.lunaris.api.world.Dimension;

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
