package org.lunaris.material.block;

import org.lunaris.block.Block;
import org.lunaris.entity.Entity;
import org.lunaris.material.Material;

/**
 * @author xtrafrancyz
 */
public class BlockWaterStill extends LiquidStaticBlock {
    public BlockWaterStill() {
        super(Material.WATER_STILL, "Still Water");
    }

    @Override
    public void onEntityCollide(Block block, Entity entity) {
        super.onEntityCollide(block, entity);

        //extinguish
        if (entity.getFireTicks() > 0)
            entity.setOnFire(0);
    }

    @Override
    public int tickRate(Block block) {
        return 5;
    }
}
