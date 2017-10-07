package org.lunaris.material.block.liquid;

import org.lunaris.block.Block;
import org.lunaris.entity.Entity;
import org.lunaris.material.Material;

/**
 * @author xtrafrancyz
 */
public class BlockWater extends LiquidDynamicBlock {
    protected BlockWater() {
        super(Material.WATER, "Water");
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
