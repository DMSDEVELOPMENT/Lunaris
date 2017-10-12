package org.lunaris.inventory.transaction;

import org.lunaris.inventory.LInventory;

import java.util.Set;

/**
 * Created by RINES on 01.10.17.
 */
public interface InventoryTransaction {

    long getCreationTime();

    Set<InventoryAction> getActions();

    Set<LInventory> getInventories();

    void addAction(InventoryAction action);

    boolean canExecute();

    boolean execute();

    boolean hasExecuted();

}
