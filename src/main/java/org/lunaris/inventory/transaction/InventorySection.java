package org.lunaris.inventory.transaction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by RINES on 01.10.17.
 */
public enum InventorySection {
    NONE(-1),
    INVENTORY(0),
    FIRST(1),
    LAST(100),
    OFFHAND(119),
    ARMOR(120),
    CREATIVE(121),
    HOTBAR(122),
    FIXED_INVENTORY(123),
    CURSOR(124);

    private final static Map<Integer, InventorySection> byIds = new HashMap<>();

    static {
        for(InventorySection section : values())
            byIds.put(section.id, section);
    }

    public static InventorySection getById(int id) {
        return byIds.get(id);
    }

    private final int id;

    InventorySection(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

}
