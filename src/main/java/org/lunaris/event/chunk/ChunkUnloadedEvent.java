package org.lunaris.event.chunk;

import org.lunaris.event.Event;
import org.lunaris.world.Chunk;

/**
 * Created by RINES on 15.09.17.
 */
public class ChunkUnloadedEvent extends Event {

    private final Chunk chunk;

    public ChunkUnloadedEvent(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }

}
