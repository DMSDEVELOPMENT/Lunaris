package org.lunaris.material.block.liquid;

import org.lunaris.block.BUFlag;
import org.lunaris.block.Block;
import org.lunaris.material.Material;

/**
 * @author xtrafrancyz
 */
public abstract class LiquidStaticBlock extends LiquidBlock {
    protected LiquidStaticBlock(Material type, String name) {
        super(type, name);
    }

    @Override
    public void onNeighborBlockChange(Block block, Block neighborBlock) {
        if (!this.checkForMixing(block)) {
            block.setTypeAndData(getFlowingType(), block.getData(), BUFlag.SEND_PACKET);
            block.getWorld().scheduleUpdate(block, this.tickRate(block));
        }
    }
}