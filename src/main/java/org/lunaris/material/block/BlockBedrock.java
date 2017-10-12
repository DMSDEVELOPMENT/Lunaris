package org.lunaris.material.block;

import org.lunaris.api.item.ItemStack;
import org.lunaris.api.material.Material;

/**
 * @author xtrafrancyz
 */
public class BlockBedrock extends SolidBlock {
    public BlockBedrock() {
        super(Material.BEDROCK, "Bedrock");
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public double getResistance() {
        return 18000000;
    }

    @Override
    public boolean isBreakable(ItemStack item) {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
