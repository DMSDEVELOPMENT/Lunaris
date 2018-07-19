package org.lunaris.api.event.chunk;

import org.lunaris.api.event.Event;
import org.lunaris.api.world.Chunk;

/**
 * Called after chunk has been unloaded.
 * Created by RINES on 15.09.17.
 */
public class ChunkUnloadedEvent extends Event {

    private final Chunk chunk;

    public ChunkUnloadedEvent(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return this.chunk;
    }

}
