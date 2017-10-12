package org.lunaris.material.block;

import org.lunaris.api.entity.Player;
import org.lunaris.api.world.Block;
import org.lunaris.block.BlockColor;
import org.lunaris.api.world.BlockFace;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.item.ItemToolType;
import org.lunaris.api.material.Material;
import org.lunaris.material.LItemHandle;

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
            ((LItemHandle) item.getItemHandle()).useOn(item, block, BlockFace.UP, player);
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
