package org.lunaris.inventory.transaction.action;

import org.lunaris.entity.Player;
import org.lunaris.entity.data.Gamemode;
import org.lunaris.inventory.transaction.InventoryAction;
import org.lunaris.item.ItemStack;
import org.lunaris.network.protocol.packet.Packet1EInventoryTransaction;

/**
 * Created by RINES on 01.10.17.
 */
public class CreativeInventoryAction extends InventoryAction {

    private final Packet1EInventoryTransaction.CreativeMagicActionType type;

    public CreativeInventoryAction(Packet1EInventoryTransaction.CreativeMagicActionType type, ItemStack sourceItem, ItemStack targetItem) {
        super(sourceItem, targetItem);
        this.type = type;
    }

    public Packet1EInventoryTransaction.CreativeMagicActionType getType() {
        return this.type;
    }

    @Override
    public boolean isValid(Player source) {
        return source.getGamemode() == Gamemode.CREATIVE &&
                (this.type == Packet1EInventoryTransaction.CreativeMagicActionType.DELETE_ITEM || getSourceItem().canBeFoundInCreative());
    }

    @Override
    public boolean execute(Player source) {
        return true;
    }

    @Override
    public void onExecuteSuccess(Player source) {

    }

    @Override
    public void onExecuteFail(Player source) {

    }

}
