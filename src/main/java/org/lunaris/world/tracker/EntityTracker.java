package org.lunaris.world.tracker;

import org.lunaris.LunarisServer;
import org.lunaris.entity.LEntity;
import org.lunaris.entity.LPlayer;
import org.lunaris.network.Packet;
import org.lunaris.network.packet.Packet0ERemoveEntity;
import org.lunaris.world.LWorld;
import org.lunaris.world.util.LongObjectHashMap;

import java.util.Set;

/**
 * @author xtrafrancyz
 */
public class EntityTracker {
    private final LunarisServer server;
    private final LWorld world;
    private LongObjectHashMap<TrackedEntity> entities;

    public EntityTracker(LunarisServer server, LWorld world) {
        this.server = server;
        this.world = world;
        this.entities = new LongObjectHashMap<>();
    }

    public void track(LEntity entity) {
        if (entity instanceof LPlayer) {
            registerEntity(entity, entity.getTrackRange(), 2);
        } else {
            registerEntity(entity, entity.getTrackRange(), 3);
        }
    }

    public void untrack(LEntity entity) {
        TrackedEntity tracked = entities.remove(entity.getEntityID());
        if (tracked != null)
            tracked.sendPacket(new Packet0ERemoveEntity(entity.getEntityID()));
    }

    private void registerEntity(LEntity entity, int viewDistance, int updateFrequency) {
        if (entities.containsKey(entity.getEntityID())) {
            server.getLogger().warn("Duplicate entity in tracker " + entity);
            return;
        }
        if (entity instanceof LPlayer) {
            for (TrackedEntity tracked : entities.values())
                tracked.updatePlayer((LPlayer) entity);
        }
        TrackedEntity tracked = new TrackedEntity(entity, updateFrequency, viewDistance);
        entities.put(entity.getEntityID(), tracked);
        tracked.updatePlayers(world.getPlayers());
    }

    public void sendPacketToWatchers(LEntity entity, Packet packet) {
        entities.get(entity.getEntityID()).sendPacket(packet);
    }

    public Set<LPlayer> getWatchers(LEntity entity) {
        return entities.get(entity.getEntityID()).getTrackingPlayers();
    }

    public void tick() {
        for (TrackedEntity entity : entities.values()) {
            entity.update(world.getPlayers());
        }
    }
}
