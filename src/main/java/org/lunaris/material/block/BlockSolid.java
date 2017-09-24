package org.lunaris.material.block;

import org.lunaris.block.BlockColor;
import org.lunaris.material.BlockMaterial;
import org.lunaris.material.Material;

/**
 * Created by RINES on 24.09.17.
 */
public abstract class BlockSolid extends BlockMaterial {

    protected BlockSolid(Material material, String name) {
        super(material, name);
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

}
