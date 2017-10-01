package org.lunaris.inventory.transaction.action;

import org.lunaris.entity.Player;
import org.lunaris.inventory.transaction.InventoryAction;
import org.lunaris.item.ItemStack;
import org.lunaris.material.Material;

/**
 * Created by RINES on 01.10.17.
 */
public class DropItemAction extends InventoryAction {

    public DropItemAction(ItemStack sourceItem, ItemStack targetItem) {
        super(sourceItem, targetItem);
    }

    @Override
    public boolean isValid(Player source) {
        return getSourceItem() == null || getSourceItem().getType() == Material.AIR;
    }

    @Override
    public boolean execute(Player source) {
        //drop event
        //drop item
        return true;
    }

    @Override
    public void onExecuteSuccess(Player source) {

    }

    @Override
    public void onExecuteFail(Player source) {

    }
}
