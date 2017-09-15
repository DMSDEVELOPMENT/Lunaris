package org.lunaris.event.chunk;

import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;

/**
 * Created by RINES on 15.09.17.
 */
public class ChunkPreLoadEvent extends Event implements Cancellable {

    private final int chunkX, chunkZ;
    private boolean cancelled;

    public ChunkPreLoadEvent(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

}
