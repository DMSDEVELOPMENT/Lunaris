package org.lunaris.material.block;

import org.lunaris.api.entity.Player;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.item.ItemToolType;
import org.lunaris.api.material.Material;
import org.lunaris.api.world.Block;
import org.lunaris.entity.LPlayer;
import org.lunaris.util.math.AxisAlignedBB;
import org.lunaris.world.LWorld;
import org.lunaris.world.tileentity.ChestTileEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RINES on 12.10.17.
 */
public class BlockChest extends TransparentBlock {

    public BlockChest() {
        super(Material.CHEST, "Chest");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 2.5;
    }

    @Override
    public double getResistance() {
        return 12.5;
    }

    @Override
    public ItemToolType getRequiredToolType() {
        return ItemToolType.AXE;
    }

    @Override
    public AxisAlignedBB recalculateBoundingBox(Block block) {
        return new AxisAlignedBB(
                block.getX() + 0.0625F,
                block.getY(),
                block.getZ() + 0.0625F,
                block.getX() + 0.9375F,
                block.getY() + 0.9475F,
                block.getZ() + 0.9375F
        );
    }

    @Override
    public void onBlockAdd(Block block) {
        ((LWorld) block.getWorld()).registerTileEntity(new ChestTileEntity(block.getLocation()));
    }

    @Override
    public boolean onBreak(ItemStack item, Block block) {
        LWorld world = (LWorld) block.getWorld();
        world.unregisterTileEntity(world.getTileEntityAt(block.getLocation()));
        return true;
    }

    @Override
    public boolean onActivate(Block block, ItemStack item, Player player) {
        ((LPlayer) player).openInventory(((ChestTileEntity) ((LWorld) block.getWorld()).getTileEntityAt(block.getLocation())).getInventory());
        return true;
    }

    @Override
    public List<ItemStack> getDrops(Block block, ItemStack hand) {
        List<ItemStack> items = new ArrayList<>();
        items.add(new ItemStack(Material.CHEST));
        for(ItemStack is : ((ChestTileEntity) ((LWorld) block.getWorld()).getTileEntityAt(block.getLocation())).getInventory())
            if(is != null && is.getType() != Material.AIR)
                items.add(is);
        return items;
    }

}
