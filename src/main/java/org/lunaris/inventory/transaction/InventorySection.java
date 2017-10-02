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

    private final static Map<Byte, InventorySection> byIds = new HashMap<>();

    static {
        for(InventorySection section : values())
            byIds.put(section.id, section);
    }

    public static InventorySection getById(int id) {
        return byIds.get((byte) id);
    }

    private final byte id;

    InventorySection(int id) {
        this.id = (byte) id;
    }

    public byte getId() {
        return this.id;
    }

}
