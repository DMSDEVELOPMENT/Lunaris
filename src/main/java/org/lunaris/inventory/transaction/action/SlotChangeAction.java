package org.lunaris.inventory.transaction.action;

import org.lunaris.api.entity.Player;
import org.lunaris.entity.LPlayer;
import org.lunaris.inventory.LInventory;
import org.lunaris.inventory.transaction.InventoryAction;
import org.lunaris.api.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by RINES on 01.10.17.
 */
public class SlotChangeAction extends InventoryAction {

    private final LInventory inventory;
    private final int slot;

    public SlotChangeAction(LInventory inventory, int slot, ItemStack sourceItem, ItemStack targetItem) {
        super(sourceItem, targetItem);
        this.inventory = inventory;
        this.slot = slot;
    }

    public LInventory getInventory() {
        return this.inventory;
    }

    public int getSlot() {
        return this.slot;
    }

    @Override
    public boolean isValid(LPlayer source) {
//        System.out.println(this.inventory.getClass().getSimpleName() + " " + this.inventory.getItem(this.slot) + " " + getSourceItem());
        return this.inventory.getItem(this.slot).equals(getSourceItem());
    }

    @Override
    public boolean execute(LPlayer source) {
        this.inventory.setItemWithoutUpdate(this.slot, getTargetItem());
        return true;
    }

    @Override
    public void onExecuteSuccess(LPlayer source) {
        Set<Player> players = new HashSet<>(this.inventory.getViewers());
        players.remove(source);
        this.inventory.sendSlot(players, this.slot);
    }

    @Override
    public void onExecuteFail(LPlayer source) {
        this.inventory.setItemWithoutUpdate(this.slot, getSourceItem());
    }

}
