package org.lunaris.material.block;

import org.lunaris.block.Block;
import org.lunaris.material.Material;
import org.lunaris.util.math.AxisAlignedBB;

/**
 * Created by RINES on 24.09.17.
 */
public abstract class FlowableBlock extends TransparentBlock {

    protected FlowableBlock(Material type, String name) {
        super(type, name);
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public boolean canPassThrough() {
        return true;
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
    public boolean isSolid() {
        return false;
    }

}
