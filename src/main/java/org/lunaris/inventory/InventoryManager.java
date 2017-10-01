package org.lunaris.inventory;

import org.lunaris.entity.Player;
import org.lunaris.inventory.transaction.InventorySection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by RINES on 01.10.17.
 */
public class InventoryManager {

    private final Player player;
    private final Map<Inventory, Integer> inventories = new HashMap<>();
    private final Map<Integer, Inventory> reverted = new HashMap<>();
    private final Set<Integer> permamentInventories = new HashSet<>();

    private int idIncrementor = 4;
    private int lastOpenedInventory = -1;

    private final PlayerInventory playerInventory;
    private final CursorInventory cursorInventory;

    public InventoryManager(Player player) {
        this.player = player;
        addInventory(this.playerInventory = new PlayerInventory(player), InventorySection.INVENTORY.getId());
        addInventory(this.cursorInventory = new CursorInventory(player), InventorySection.CURSOR.getId());
    }

    public int getInventoryId(Inventory inventory) {
        Integer id = this.inventories.get(inventory);
        return id == null ? -1 : id;
    }

    public Inventory getInventoryById(int id) {
        return this.reverted.get(id);
    }

    public int addInventory(Inventory inventory) {
        return addInventory(inventory, null);
    }

    public int addInventory(Inventory inventory, Integer forcedId) {
        return addInventory(inventory, forcedId, false);
    }

    public int addInventory(Inventory inventory, Integer forceId, boolean permament) {
        int id = getInventoryId(inventory);
        if(id != -1)
            return id;
        id = forceId == null ? this.idIncrementor = Math.max(4, ++this.idIncrementor % 99) : forceId;
        this.inventories.put(inventory, id);
        this.reverted.put(id, inventory);
        if(permament)
            this.permamentInventories.add(id);
        if(inventory.open(this.player))
            return id;
        else {
            removeInventory(inventory);
            return -1;
        }
    }

    public void removeInventory(Inventory inventory) {
        inventory.close(this.player);
        Integer id = this.inventories.remove(inventory);
        if(id != null)
            this.reverted.remove(id);
    }

    public void sendInventory(Inventory inventory) {
        inventory.sendContents(this.player);
    }

    public void sendAllInventories() {
        this.inventories.keySet().forEach(this::sendInventory);
    }

    public void closeAndRemoveLastOpenedInventory() {
        Inventory inventory = this.reverted.get(this.lastOpenedInventory);
        if(inventory == null)
            return;
        removeInventory(inventory);
        //close
    }

    public PlayerInventory getPlayerInventory() {
        return this.playerInventory;
    }
}
