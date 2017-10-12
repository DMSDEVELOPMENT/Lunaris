package org.lunaris.api.inventory;

import org.lunaris.api.entity.Player;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.material.Material;

import java.util.Collection;
import java.util.Map;

/**
 * Created by RINES on 12.10.17.
 */
public interface Inventory extends Iterable<ItemStack> {

    /**
     * Get this inventory type.
     * @return this inventory type.
     */
    InventoryType getType();

    /**
     * Get this inventory title.
     * @return this inventory title.
     */
    String getTitle();

    /**
     * @see Inventory#getTitle()
     * @return this inventory title.
     */
    default String getName() {
        return getTitle();
    }

    /**
     * Get this inventory size.
     * @return this inventory size.
     */
    int size();

    /**
     * @see Inventory#size()
     * @return this inventory size.
     */
    default int getSize() {
        return size();
    }

    /**
     * Get itemstack at specified index in this inventory.
     * This method never returns null.
     * @param index slot number.
     * @return itemstack at specified slot.
     * @throws ArrayIndexOutOfBoundsException if index is over inventory's size.
     */
    ItemStack getItem(int index);

    /**
     * Get array of all itemstacks in inventory. They will be located at indices, corresponding to their slot-ids.
     * Resulting array can contain nulls (whether slot is empty) and itemstacks with air-material.
     * @return array of all itemstacks in inventory.
     */
    ItemStack[] getContents();

    /**
     * Replaces current inventory contents with new one.
     * Given array can be smaller than inventory size, bat can't be bigger.
     * @param contents new inventory contents.
     */
    void setContents(ItemStack[] contents);

    /**
     * Set slot at given index in the inventory.
     * @param index slot identifier.
     * @param item the itemstack to be placed at specified slot in this inventory.
     */
    void setItem(int index, ItemStack item);

    /**
     * Check whether there is at least one itemstack of given material in this inventory.
     * @param type the material to look for.
     * @return if there is at least one itemstack of given material in this inventory.
     */
    boolean contains(Material type);

    /**
     * Check whether there is at least one itemstack of given material id in this inventory.
     * @param id identifier of the material to look for.
     * @return if there is at least one itemstack of given material id in this inventory.
     */
    boolean contains(int id);

    /**
     * Check whether there is given itemstack in the inventory.
     * Check includes comparing items material, data, amount and nbt tags.
     * @param item itemstack to look for.
     * @return if there is given itemstack in the inventory.
     */
    boolean contains(ItemStack item);

    /**
     * Check whether there are at least given amount of itemstacks with given material.
     * @param type the material to look for.
     * @param amount minimal required amount.
     * @return if there are at least given amount of itemstacks with given material.
     */
    boolean contains(Material type, int amount);


    /**
     * Check whether there are at least given amount of itemstacks with given material id.
     * @param id identifier of the material to look for.
     * @param amount minimal required amount.
     * @return if there are at least given amount of itemstacks with given material id.
     */
    boolean contains(int id, int amount);

    /**
     * Check whether there are at least given amount of given itemstacks.
     * Itemstacks comparison includes checking itemstacks types, datas and nbt tags.
     * @param item itemstack to look for.
     * @param amount minimal required amount.
     * @return if there are at least given amount of given itemstacks.
     */
    boolean contains(ItemStack item, int amount);

    /**
     * Get index of first empty (air-filled) slot.
     * @return -1, whether there are no empty slots; slot index otherwise.
     */
    int firstEmpty();

    /**
     * Add items to the inventory.
     * @param items what you want to add to the inventory.
     * @return map, keys in which are indices of the input array of items, which were not added fully (because of not
     * enough space in the inventory); values - not added itemstacks with edited amounts (amount left - how many items
     * of that type (type - is not material, but the item itself from your array) were not added to the inventory).
     */
    Map<Integer, ItemStack> addItem(ItemStack... items);

    /**
     * Remove items from the inventory.
     * @param items what you want to be removed from the inventory.
     * @return map, keys in which are indices of the input array of items, which were not fully removed from the
     * inventory (because there were not enough items of given types in the inventory); values - not removed itemstacks
     * with edited amounts (amount left - how many items were not removed from the inventory).
     */
    Map<Integer, ItemStack> removeItem(ItemStack... items);

    /**
     * Remove all items from the inventory.
     */
    void clear();

    /**
     * Get all players, who have this inventory opened or have actual information about it.
     * For example, PlayerInventory holder always has actual information about his own inventory.
     * @return collection of this inventory viewers.
     */
    Collection<? extends Player> getViewers();

}
