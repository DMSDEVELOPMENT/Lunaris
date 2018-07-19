package org.lunaris.api.event.chunk;

import org.lunaris.api.event.Event;
import org.lunaris.api.world.Chunk;

/**
 * Called after chunk is being loaded.
 * Created by RINES on 15.09.17.
 */
public class ChunkLoadedEvent extends Event {

    private final Chunk chunk;

    public ChunkLoadedEvent(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return this.chunk;
    }

}
