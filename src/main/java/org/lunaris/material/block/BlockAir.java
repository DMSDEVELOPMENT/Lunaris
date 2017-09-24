package org.lunaris.material.block;

import org.lunaris.block.Block;
import org.lunaris.item.ItemStack;
import org.lunaris.material.Material;
import org.lunaris.util.math.AxisAlignedBB;

/**
 * Created by RINES on 24.09.17.
 */
public class BlockAir extends BlockTransparent {

    public BlockAir() {
        super(Material.AIR, "Air");
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean isBreakable(ItemStack item) {
        return false;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public boolean canBePlaced() {
        return false;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(Block block) {
        return null;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
