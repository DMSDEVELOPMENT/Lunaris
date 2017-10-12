package org.lunaris.material.block.liquid;

import org.lunaris.api.world.Block;
import org.lunaris.material.Material;
import org.lunaris.world.Dimension;

/**
 * @author xtrafrancyz
 */
public class BlockLavaStill extends LiquidStaticBlock {
    public BlockLavaStill() {
        super(Material.LAVA_STILL, "Still Lava");
    }

    @Override
    public int tickRate(Block block) {
        return block.getWorld().getDimension() == Dimension.THE_NETHER ? 5 : 30;
    }
}
