package org.lunaris.block;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by RINES on 13.09.17.
 */
public enum Material {
    AIR(0),
    STONE(1),
    GRASS(2);

    private final static Map<Integer, Material> BY_ID = new HashMap<>();

    static {
        for(Material material : values())
            BY_ID.put(material.id, material);
    }

    public static Material getById(int id) {
        return BY_ID.get(id);
    }

    private final int id;

    Material(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

}
