package org.lunaris.inventory;

import org.lunaris.item.ItemStack;
import org.lunaris.material.Material;

/**
 * Created by RINES on 16.09.17.
 */
public class PlayerInventory {

    public ItemStack getItemInHand() {
        return new ItemStack(Material.AIR, 1);
    }

}
