package org.lunaris.api.inventory;

import org.lunaris.api.item.ItemStack;

/**
 * Created by RINES on 12.10.17.
 */
public interface EquipmentInventory {

    ItemStack getHelmet();

    ItemStack getChestplate();

    ItemStack getLeggings();

    ItemStack getBoots();

    ItemStack getItemInHand();

    ItemStack[] getArmorContents();

    void setHelmet(ItemStack item);

    void setChestplate(ItemStack chestplate);

    void setLeggings(ItemStack leggings);

    void setBoots(ItemStack boots);

    void setItemInHand(ItemStack item);

}
