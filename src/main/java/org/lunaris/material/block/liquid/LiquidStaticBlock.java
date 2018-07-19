package org.lunaris.material.block.liquid;

import org.lunaris.api.material.Material;
import org.lunaris.api.world.Block;
import org.lunaris.block.BUFlag;
import org.lunaris.block.LBlock;

/**
 * @author xtrafrancyz
 */
public abstract class LiquidStaticBlock extends LiquidBlock {

    protected LiquidStaticBlock(Material type, String name) {
        super(type, name);
    }

    @Override
    public void onNeighborBlockChange(Block b, Block neighborBlock) {
        LBlock block = (LBlock) b;
        if (!this.checkForMixing(block)) {
            block.setTypeAndData(getFlowingType(), block.getData(), BUFlag.SEND_PACKET);
            block.getWorld().scheduleUpdate(block, this.tickRate(block));
        }
    }

}
