package org.lunaris.material.block;

import org.lunaris.api.material.Material;
import org.lunaris.block.BlockColor;
import org.lunaris.material.DyeColor;

/**
 * @author xtrafrancyz
 */
public abstract class ColoredBlock extends SolidBlock {
    protected ColoredBlock(Material type, String name) {
        super(type, name);
    }

    @Override
    public String getName(int data) {
        return DyeColor.getByWoolData(data).getName() + " " + super.getName(data);
    }

    @Override
    public BlockColor getColor(int data) {
        return DyeColor.getByWoolData(data).getColor();
    }
}
