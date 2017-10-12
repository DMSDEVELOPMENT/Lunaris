package org.lunaris.inventory;

import org.lunaris.entity.LPlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by RINES on 01.10.17.
 */
public class InventoryManager {

    private final static CreativeInventory creative = new CreativeInventory();

    private final LPlayer player;
    private final Map<LInventory, Integer> inventories = new HashMap<>();
    private final Map<Integer, LInventory> reverted = new HashMap<>();
    private final Set<Integer> permamentInventories = new HashSet<>();

    private int idIncrementor = 4;

    private final PlayerInventory playerInventory;
    private final CursorInventory cursorInventory;

    public InventoryManager(LPlayer player) {
        this.player = player;
        addInventory(this.playerInventory = new PlayerInventory(player), true);
        addInventory(this.cursorInventory = new CursorInventory(player), true);
        this.inventories.put(creative, creative.getReservedInventoryId());
        this.reverted.put(creative.getReservedInventoryId(), creative);
    }

    public int getInventoryId(LInventory inventory) {
        Integer id = this.inventories.get(inventory);
        return id == null ? -1 : id;
    }

    public LInventory getInventoryById(int id) {
        return this.reverted.get(id);
    }

    public int addInventory(LInventory inventory) {
        return addInventory(inventory, inventory.getReservedInventoryId() == -1 ? null : inventory.getReservedInventoryId());
    }

    public int addInventory(LInventory inventory, boolean permament) {
        return addInventory(inventory, inventory.getReservedInventoryId() == -1 ? null : inventory.getReservedInventoryId(), permament);
    }

    public int addInventory(LInventory inventory, Integer forcedId) {
        return addInventory(inventory, forcedId, false);
    }

    public int addInventory(LInventory inventory, Integer forceId, boolean permament) {
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

    public void removeInventory(LInventory inventory) {
        inventory.close(this.player);
        Integer id = this.inventories.remove(inventory);
        if(id != null)
            this.reverted.remove(id);
    }

    public void sendInventory(LInventory inventory) {
        inventory.sendContents(this.player);
    }

    public void sendAllInventories() {
        this.inventories.keySet().forEach(this::sendInventory);
    }

    public void closeInventory(int inventoryID) {
        LInventory inventory = this.reverted.get(inventoryID);
        if(inventory == null)
            return;
        removeInventory(inventory);
    }

    public CreativeInventory getCreativeInventory() {
        return creative;
    }

    public PlayerInventory getPlayerInventory() {
        return this.playerInventory;
    }

    public CursorInventory getCursorInventory() {
        return this.cursorInventory;
    }

}
