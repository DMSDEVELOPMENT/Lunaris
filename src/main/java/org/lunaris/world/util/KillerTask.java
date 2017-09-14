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
public class KillerTask {

    private final static int LAUNCH_DELAY = 20 * 30;

    private final IServer server;

    private final World world;

    private final LongObjectHashMap<Chunk> chunks;

    private final Set<Chunk> toBeUnloaded = new HashSet<>();

    private int ticks;

    public KillerTask(IServer server, World world, LongObjectHashMap<Chunk> chunks) {
        this.server = server;
        this.world = world;
        this.chunks = chunks;
    }

    public void tick() {
        if(++this.ticks == LAUNCH_DELAY) {
            this.ticks = 0;
            Set<Chunk> chunks = new HashSet<>();
            chunks.addAll(this.chunks.values());
            Set<Location> players = this.world.getPlayers().stream().map(Player::getLocation).collect(Collectors.toSet());
            this.server.getScheduler().runAsync(() -> {
                chunkCycle:
                for(Iterator<Chunk> iterator = chunks.iterator(); iterator.hasNext();) {
                    Chunk chunk = iterator.next();
                    for(Location location : players)
                        if(this.world.isInRangeOfView(location, chunk))
                            continue chunkCycle;
                    iterator.remove();
                }
                synchronized(this.toBeUnloaded) {
                    this.toBeUnloaded.clear();
                    this.toBeUnloaded.addAll(chunks);
                }
            });
            synchronized(this.toBeUnloaded) {
                for(Chunk chunk : this.toBeUnloaded)
                    this.world.unloadChunk(chunk);
                this.toBeUnloaded.clear();
            }
        }
    }

}
