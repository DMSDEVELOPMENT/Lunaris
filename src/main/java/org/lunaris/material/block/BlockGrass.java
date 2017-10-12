package org.lunaris.material.block;

import org.lunaris.api.entity.Player;
import org.lunaris.api.world.Block;
import org.lunaris.api.world.BlockFace;
import org.lunaris.item.ItemStack;
import org.lunaris.item.ItemToolType;
import org.lunaris.material.Material;

/**
 * Created by RINES on 24.09.17.
 */
public class BlockGrass extends BlockDirt {

    protected BlockGrass(Material type, String name) {
        super(type, name);
    }

    protected BlockGrass() {
        super(Material.GRASS, "Grass");
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public boolean onActivate(Block block, ItemStack item, Player player) {
        if (item != null && item.isItem()) {
            if (item.isOfToolType(ItemToolType.HOE)) {
                item.getItemHandle().useOn(item, block, BlockFace.UP, player);
                block.setType(Material.FARM_LAND);
                return true;
            } else if (item.isOfToolType(ItemToolType.SHOVEL)) {
                item.getItemHandle().useOn(item, block, BlockFace.UP, player);
                block.setType(Material.GRASS_PATH);
                return true;
            }
            /*if (item.getType() == Material.DYE && item.getData() == 0x0F) {
                item.setAmount(item.getAmount() - 1);
                ObjectTallGrass.growGrass(block, new NukkitRandom(), 15, 10);
                return true;
            }*/
        }
        return false;
    }

}
