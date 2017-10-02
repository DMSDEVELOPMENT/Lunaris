package org.lunaris.material.block;

import org.lunaris.block.BlockColor;
import org.lunaris.material.DyeColor;
import org.lunaris.material.Material;

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
