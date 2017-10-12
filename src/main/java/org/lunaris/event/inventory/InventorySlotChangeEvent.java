package org.lunaris.event.inventory;

import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;
import org.lunaris.inventory.Inventory;
import org.lunaris.api.item.ItemStack;

/**
 * Created by RINES on 01.10.17.
 */
public class InventorySlotChangeEvent extends Event implements Cancellable {

    private final Inventory inventory;
    private final int slot;
    private final ItemStack previousItem;
    private ItemStack newItem;
    private boolean cancelled;

    public InventorySlotChangeEvent(Inventory inventory, int slot, ItemStack previousItem, ItemStack newItem) {
        this.inventory = inventory;
        this.slot = slot;
        this.previousItem = previousItem;
        this.newItem = newItem;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public int getSlot() {
        return this.slot;
    }

    public ItemStack getPreviousItem() {
        return this.previousItem;
    }

    public ItemStack getNewItem() {
        return this.newItem;
    }

    public void setNewItemStack(ItemStack item) {
        this.newItem = item;
    }

}
