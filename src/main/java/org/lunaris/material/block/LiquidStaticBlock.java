package org.lunaris.material.block;

import org.lunaris.block.Block;
import org.lunaris.material.Material;

/**
 * @author xtrafrancyz
 */
public abstract class LiquidStaticBlock extends LiquidBlock {
    protected LiquidStaticBlock(Material material, String name) {
        super(material, name);
    }

    @Override
    public void onNeighborBlockChange(Block block, Block neighborBlock) {
        if (!this.checkForMixing(block)) {
            block.setTypeAndData(getFlowingType(), block.getData());
            block.getWorld().scheduleUpdate(block, this.tickRate(block));
        }
    }
}
