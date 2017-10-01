package org.lunaris.inventory.transaction;

import org.lunaris.entity.Player;
import org.lunaris.inventory.Inventory;
import org.lunaris.inventory.transaction.action.SlotChangeAction;
import org.lunaris.item.ItemStack;
import org.lunaris.material.Material;

import java.util.*;

/**
 * Created by RINES on 01.10.17.
 */
public class BasicInventoryTransaction implements InventoryTransaction {

    private final Player player;
    private final Set<Inventory> inventories = new HashSet<>();
    private final Set<InventoryAction> actions = new HashSet<>();
    private final long creationTime;
    private boolean hasExecuted;

    public BasicInventoryTransaction(Player player, List<InventoryAction> actions) {
        this.player = player;
        actions.forEach(this::addAction);
        this.creationTime = System.currentTimeMillis();
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public Set<InventoryAction> getActions() {
        return this.actions;
    }

    @Override
    public Set<Inventory> getInventories() {
        return this.inventories;
    }

    @Override
    public void addAction(InventoryAction action) {
        if(this.actions.contains(action))
            return;
        if(action instanceof SlotChangeAction)
            this.inventories.add(((SlotChangeAction) action).getInventory());
        this.actions.add(action);
    }

    protected boolean matchItems(List<ItemStack> needItems, List<ItemStack> haveItems) {
        for (InventoryAction action : this.actions) {
            ItemStack target = action.getTargetItem();
            if(target != null && target.getType() != Material.AIR)
                needItems.add(target);
            if(!action.isValid(this.player)) {
                System.out.println("IS NOT VALID");
                return false;
            }
            target = action.getSourceItem();
            if(target != null && target.getType() != Material.AIR)
                haveItems.add(target);
        }
        for(ItemStack needItem : new ArrayList<>(needItems)) {
            int left = needItem.getAmount();
            for(ItemStack haveItem : new ArrayList<>(haveItems)) {
                if(needItem.isSimilar(haveItem)) {
                    int min = Math.min(haveItem.getAmount(), left);
                    haveItem.setAmount(haveItem.getAmount() - min);
                    if(haveItem.getAmount() == 0)
                        haveItems.remove(haveItem);
                    if((left -= min) == 0) {
                        needItems.remove(needItem);
                        break;
                    }
                }
            }
        }
        return haveItems.isEmpty() && needItems.isEmpty();
    }

    private void reorderActions() {

    }

    /**
     * Iterates over SlotChangeActions in this transaction and compacts any which refer to the same inventorySlot in the same
     * inventory so they can be correctly handled.
     * <p>
     * Under normal circumstances, the same inventorySlot would never be changed more than once in a single transaction. However,
     * due to the way things like the crafting grid are "implemented" in MCPE 1.2 (a.k.a. hacked-in), we may get
     * multiple inventorySlot changes referring to the same inventorySlot in a single transaction. These multiples are not even guaranteed
     * to be in the correct order (inventorySlot splitting in the crafting grid for example, causes the actions to be sent in the
     * wrong order), so this method also tries to chain them into order.
     *
     * @return bool
     */
    protected boolean squashDuplicateSlotChanges() {
        Map<Integer, List<SlotChangeAction>> slotChanges = new HashMap<>();

        for (InventoryAction action : this.actions) {
            if (action instanceof SlotChangeAction) {
                int hash = Objects.hash(((SlotChangeAction) action).getInventory(), ((SlotChangeAction) action).getSlot());

                List<SlotChangeAction> list = slotChanges.get(hash);
                if (list == null) {
                    list = new ArrayList<>();
                }

                list.add((SlotChangeAction) action);

                slotChanges.put(hash, list);
            }
        }

        for (Map.Entry<Integer, List<SlotChangeAction>> entry : new ArrayList<>(slotChanges.entrySet())) {
            int hash = entry.getKey();
            List<SlotChangeAction> list = entry.getValue();

            if (list.size() == 1) { //No need to compact inventorySlot changes if there is only one on this inventorySlot
                slotChanges.remove(hash);
                continue;
            }

            List<SlotChangeAction> originalList = new ArrayList<>(list);

            SlotChangeAction originalAction = null;
            ItemStack lastTargetItem = null;

            for (int i = 0; i < list.size(); i++) {
                SlotChangeAction action = list.get(i);

                if (action.isValid(this.player)) {
                    originalAction = action;
                    lastTargetItem = action.getTargetItem();
                    list.remove(i);
                    break;
                }
            }

            if (originalAction == null) {
                return false; //Couldn't find any actions that had a source-item matching the current inventory inventorySlot
            }

            int sortedThisLoop;

            do {
                sortedThisLoop = 0;
                for (int i = 0; i < list.size(); i++) {
                    SlotChangeAction action = list.get(i);

                    ItemStack actionSource = action.getSourceItem();
                    if (actionSource.isSimilar(lastTargetItem)) {
                        lastTargetItem = action.getTargetItem();
                        list.remove(i);
                        sortedThisLoop++;
                    }
                    else if (actionSource.isSimilar(lastTargetItem)) {
                        lastTargetItem.setAmount(lastTargetItem.getAmount() - actionSource.getAmount());
                        list.remove(i);
                        if(lastTargetItem.getAmount() == 0)
                            ++sortedThisLoop;
                    }
                }
            } while (sortedThisLoop > 0);
            if(!list.isEmpty())
                return false;
            originalList.forEach(this.actions::remove);
            this.addAction(new SlotChangeAction(originalAction.getInventory(), originalAction.getSlot(), originalAction.getSourceItem(), lastTargetItem));
        }

        return true;
    }

    @Override
    public boolean canExecute() {
        squashDuplicateSlotChanges();
        return matchItems(new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public boolean execute() {
        if(hasExecuted() || !canExecute())
            return false;
        for(InventoryAction action : this.actions) {
            if(action.execute(this.player))
                action.onExecuteSuccess(this.player);
            else
                action.onExecuteFail(this.player);
        }
        this.hasExecuted = true;
        return true;
    }

    @Override
    public boolean hasExecuted() {
        return this.hasExecuted;
    }
}
