package org.lunaris.material.block;

import org.lunaris.block.BlockColor;
import org.lunaris.material.LBlockHandle;
import org.lunaris.api.material.Material;

/**
 * Created by RINES on 24.09.17.
 */
public abstract class SolidBlock extends LBlockHandle {

    protected SolidBlock(Material type, String name) {
        super(type, name);
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public BlockColor getColor(int data) {
        return BlockColor.STONE_BLOCK_COLOR;
    }

}
