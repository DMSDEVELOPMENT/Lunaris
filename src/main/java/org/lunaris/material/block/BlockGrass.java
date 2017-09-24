package org.lunaris.material.block;

import org.lunaris.block.Block;
import org.lunaris.entity.Player;
import org.lunaris.item.ItemStack;
import org.lunaris.item.ItemToolType;
import org.lunaris.material.Material;

/**
 * Created by RINES on 24.09.17.
 */
public class BlockGrass extends BlockDirt {

    protected BlockGrass(Material material, String name) {
        super(material, name);
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
        if(item.isOfToolType(ItemToolType.HOE)) {
            item.useOn(block, player);
            block.setType(Material.FARM_LAND);
            return true;
        }else if(item.isOfToolType(ItemToolType.SHOVEL)) {
            item.useOn(block, player);
            block.setType(Material.GRASS_PATH);
            return true;
        }
//        if (item.getId() == Item.DYE && item.getDamage() == 0x0F) {
//            item.count--;
//            ObjectTallGrass.growGrass(this.getLevel(), this, new NukkitRandom(), 15, 10);
//            return true;
//        }

        return false;
    }

}
