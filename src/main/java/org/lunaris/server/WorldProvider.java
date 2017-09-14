package org.lunaris.server;

import org.lunaris.world.Difficulty;
import org.lunaris.world.Dimension;
import org.lunaris.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by RINES on 14.09.17.
 */
public class WorldProvider {

    private final List<World> worlds = new ArrayList<>();

    public WorldProvider(IServer server) {
        this.worlds.add(new World(server, "Test World", Dimension.OVERWORLD, Difficulty.PEACEFUL));
        server.getScheduler().schedule(this::tick, 0L, 50L, TimeUnit.MILLISECONDS);
    }

    public List<World> getWorlds() {
        return this.worlds;
    }

    public World getWorld(int index) {
        return this.worlds.get(index);
    }

    public World getWorld(String name) {
        return this.worlds.stream().filter(w -> w.getName().equals(name)).findAny().orElse(null);
    }

    private void tick() {
        this.worlds.forEach(World::tick);
    }

}
