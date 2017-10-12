package org.lunaris.world.util;

import org.lunaris.entity.LPlayer;
import org.lunaris.server.IServer;
import org.lunaris.world.LChunk;
import org.lunaris.world.LWorld;

/**
 * Created by RINES on 14.09.17.
 */
public class ChunksFollowerTask {

    private final static int LAUNCH_DELAY = 20;

    private final IServer server;

    private final LWorld world;

    private int ticks;

    public ChunksFollowerTask(IServer server, LWorld world) {
        this.server = server;
        this.world = world;
    }

    public void tick() {
        if (++this.ticks == LAUNCH_DELAY) {
            this.ticks = 0;
            this.world.getPlayers().forEach(this::updatePlayer);
        }
    }
    
    public void updatePlayer(LPlayer player) {
        int cx = player.getLocation().getBlockX() >> 4, cz = player.getLocation().getBlockZ() >> 4;
        int r = player.getChunksView();
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                if (x * x + z * z <= r * r) { // делаем область кругленькой
                    LChunk chunk = this.world.loadChunk(x + cx, z + cz);
                    if (!player.hasChunkSent(chunk.getX(), chunk.getZ()))
                        chunk.sendTo(player);
                }
            }
        }
    }

}
