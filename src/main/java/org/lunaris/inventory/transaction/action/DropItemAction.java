package org.lunaris.inventory.transaction.action;

import org.lunaris.Lunaris;
import org.lunaris.entity.LPlayer;
import org.lunaris.event.player.PlayerDropItemEvent;
import org.lunaris.inventory.transaction.InventoryAction;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.material.Material;

/**
 * Created by RINES on 01.10.17.
 */
public class DropItemAction extends InventoryAction {

    public DropItemAction(ItemStack sourceItem, ItemStack targetItem) {
        super(sourceItem, targetItem);
    }

    @Override
    public boolean isValid(LPlayer source) {
        return getSourceItem() == null || getSourceItem().getType() == Material.AIR;
    }

    @Override
    public boolean execute(LPlayer source) {
        return Lunaris.getInstance().getEntityProvider().spawnItem(source.getLocation().add(0F, 1.3F, 0F), getTargetItem(), item -> {
            item.setPickupDelay(2000L);
            PlayerDropItemEvent event = new PlayerDropItemEvent(source, item);
            event.call();
            if(event.isCancelled()) {
                source.getInventory().sendContents(source);
                return false;
            }
            item.setVelocity(source.getDirection().multiply(.4F));
            return true;
        }) != null;
    }

    @Override
    public void onExecuteSuccess(LPlayer source) {

    }

    @Override
    public void onExecuteFail(LPlayer source) {

    }
}
