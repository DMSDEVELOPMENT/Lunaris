package org.lunaris.material.block;

import org.lunaris.item.ItemToolType;
import org.lunaris.material.Material;

/**
 * @author xtrafrancyz
 */
public class BlockWool extends ColoredBlock {
    protected BlockWool() {
        super(Material.WOOL, "Wool");
    }

    @Override
    public ItemToolType getRequiredToolType() {
        return ItemToolType.SHEARS;
    }
    
    @Override
    public double getHardness() {
        return 0.8;
    }

    @Override
    public double getResistance() {
        return 4;
    }

    @Override
    public int getBurnChance() {
        return 30;
    }

    @Override
    public int getBurnAbility() {
        return 60;
    }
}
