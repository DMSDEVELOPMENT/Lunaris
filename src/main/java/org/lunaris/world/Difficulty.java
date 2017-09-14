package org.lunaris.world;

/**
 * Created by RINES on 14.09.17.
 */
public enum Difficulty {
    PEACEFUL(0),
    EASY(1),
    NORMAL(2),
    HARD(3);

    private final byte id;

    Difficulty(int id) {
        this.id = (byte) id;
    }

    public byte getId() {
        return this.id;
    }
}
