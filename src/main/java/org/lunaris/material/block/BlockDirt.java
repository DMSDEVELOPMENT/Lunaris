package org.lunaris.material.block;

import org.lunaris.block.Block;
import org.lunaris.block.BlockColor;
import org.lunaris.block.BlockFace;
import org.lunaris.entity.Player;
import org.lunaris.item.ItemStack;
import org.lunaris.item.ItemToolType;
import org.lunaris.material.Material;

import java.util.Collections;
import java.util.List;

/**
 * Created by RINES on 24.09.17.
 */
public class BlockDirt extends SolidBlock {

    protected BlockDirt(Material type, String name) {
        super(type, name);
    }

    protected BlockDirt() {
        this(Material.DIRT, "Dirt");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public boolean onActivate(Block block, ItemStack item, Player player) {
        if (item != null && item.isItem() && item.isOfToolType(ItemToolType.HOE)) {
            item.getItemHandle().useOn(item, block, BlockFace.UP, player);
            block.setType(Material.FARM_LAND);
            return true;
        }
        return false;
    }

    @Override
    public List<ItemStack> getDrops(Block block, ItemStack hand) {
        return Collections.singletonList(new ItemStack(Material.DIRT, 1));
    }

    @Override
    public ItemToolType getRequiredToolType() {
        return ItemToolType.SHOVEL;
    }

    @Override
    public BlockColor getColor(int data) {
        return BlockColor.DIRT_BLOCK_COLOR;
    }

}
