package org.lunaris.inventory.transaction;

/**
 * Created by RINES on 01.10.17.
 */
public enum InventoryActionSource {
    CONTAINER(0),
    WORLD(2),
    CREATIVE(3),
    TODO(99999);

    private final int id;

    InventoryActionSource(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

}
