package org.lunaris.material.block;

import org.lunaris.block.BlockColor;
import org.lunaris.material.LBlockHandle;
import org.lunaris.api.material.Material;

/**
 * Created by RINES on 24.09.17.
 */
public abstract class TransparentBlock extends LBlockHandle {

    protected TransparentBlock(Material type, String name) {
        super(type, name);
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public BlockColor getColor(int data) {
        return BlockColor.TRANSPARENT_BLOCK_COLOR;
    }

}
