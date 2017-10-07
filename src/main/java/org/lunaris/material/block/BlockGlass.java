package org.lunaris.material.block;

import org.lunaris.material.Material;

/**
 * Created by RINES on 07.10.17.
 */
public class BlockGlass extends TransparentBlock {

    protected BlockGlass() {
        super(Material.GLASS, "glass");
    }

    @Override
    public double getResistance() {
        return 1.5;
    }

    @Override
    public double getHardness() {
        return 0.3;
    }

}
