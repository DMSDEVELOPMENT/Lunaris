package org.lunaris.world.util;

import org.lunaris.entity.LPlayer;
import org.lunaris.server.IServer;
import org.lunaris.world.LChunk;
import org.lunaris.api.world.Location;
import org.lunaris.world.LWorld;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by RINES on 14.09.17.
 */
public class ChunkUnloaderTask implements Runnable {

    private final IServer server;

    private final LWorld world;

    private final LongObjectHashMap<LChunk> chunks;

    public ChunkUnloaderTask(IServer server, LWorld world, LongObjectHashMap<LChunk> chunks) {
        this.server = server;
        this.world = world;
        this.chunks = chunks;
    }

    @Override
    public void run() {
        Set<LChunk> chunks = new HashSet<>();
        chunks.addAll(this.chunks.values());
        chunks.removeIf(c -> !c.isLoaded());
        Set<Location> players = this.world.getPlayers().stream().map(LPlayer::getLocation).collect(Collectors.toSet());
        this.server.getScheduler().runAsync(() -> {
            for (Iterator<LChunk> iterator = chunks.iterator(); iterator.hasNext(); ) {
                LChunk chunk = iterator.next();
                for (Location location : players)
                    if (this.world.isInRangeOfView(location, chunk)) {
                        iterator.remove();
                        break;
                    }
            }
            this.server.getScheduler().run(() ->
                chunks.forEach(this.world::unloadChunk)
            );
        });
    }

}
