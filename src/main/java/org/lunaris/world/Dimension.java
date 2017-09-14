package org.lunaris.world;

/**
 * Created by RINES on 14.09.17.
 */
public enum Dimension {
    OVERWORLD(0),
    THE_NETHER(1);

    private final byte id;

    Dimension(int id) {
        this.id = (byte) id;
    }

    public byte getId() {
        return this.id;
    }

}
