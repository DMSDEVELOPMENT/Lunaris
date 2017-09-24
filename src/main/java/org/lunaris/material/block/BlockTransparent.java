package org.lunaris.material.block;

import org.lunaris.block.BlockColor;
import org.lunaris.material.BlockMaterial;
import org.lunaris.material.Material;

/**
 * Created by RINES on 24.09.17.
 */
public abstract class BlockTransparent extends BlockMaterial {

    protected BlockTransparent(Material material, String name) {
        super(material, name);
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.TRANSPARENT_BLOCK_COLOR;
    }

}
