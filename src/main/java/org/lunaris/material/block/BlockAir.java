package org.lunaris.material.block;

import org.lunaris.api.item.ItemStack;
import org.lunaris.api.material.Material;

/**
 * Created by RINES on 24.09.17.
 */
public class BlockAir extends TransparentBlock {

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
