package org.lunaris.world.util;

import org.lunaris.entity.Player;
import org.lunaris.server.IServer;
import org.lunaris.world.Chunk;
import org.lunaris.world.Location;
import org.lunaris.world.World;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by RINES on 14.09.17.
 */
public class ChunkUnloaderTask {

    private final static int LAUNCH_DELAY = 20 * 30;

    private final IServer server;

    private final World world;

    private final LongObjectHashMap<Chunk> chunks;

    private int ticks;

    public ChunkUnloaderTask(IServer server, World world, LongObjectHashMap<Chunk> chunks) {
        this.server = server;
        this.world = world;
        this.chunks = chunks;
    }

    public void tick() {
        if (++this.ticks == LAUNCH_DELAY) {
            this.ticks = 0;
            Set<Chunk> chunks = new HashSet<>();
            chunks.addAll(this.chunks.values());
            Set<Location> players = this.world.getPlayers().stream().map(Player::getLocation).collect(Collectors.toSet());
            this.server.getScheduler().runAsync(() -> {
                for (Iterator<Chunk> iterator = chunks.iterator(); iterator.hasNext(); ) {
                    Chunk chunk = iterator.next();
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

}
