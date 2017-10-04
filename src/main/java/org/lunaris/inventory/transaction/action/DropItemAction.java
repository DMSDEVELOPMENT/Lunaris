package org.lunaris.inventory.transaction.action;

import org.lunaris.Lunaris;
import org.lunaris.entity.Item;
import org.lunaris.entity.Player;
import org.lunaris.event.player.PlayerDropItemEvent;
import org.lunaris.inventory.transaction.InventoryAction;
import org.lunaris.item.ItemStack;
import org.lunaris.material.Material;
import org.lunaris.util.math.Vector3d;

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
        return Lunaris.getInstance().getEntityProvider().spawnItem(source.getLocation().add(0F, 1.3F, 0F), getTargetItem(), item -> {
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
    public void onExecuteSuccess(Player source) {

    }

    @Override
    public void onExecuteFail(Player source) {

    }
}
