package org.lunaris.world.tracker;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.lunaris.LunarisServer;
import org.lunaris.entity.LEntity;
import org.lunaris.entity.LPlayer;
import org.lunaris.network.Packet;
import org.lunaris.network.packet.Packet0ERemoveEntity;
import org.lunaris.world.LWorld;

import java.util.Set;

/**
 * @author xtrafrancyz
 */
public class EntityTracker {
    private final LunarisServer server;
    private final LWorld world;
    private Long2ObjectMap<TrackedEntity> entities;
    Object2ObjectMap<LPlayer, Set<TrackedEntity>> trackedEntityLinks;

    public EntityTracker(LunarisServer server, LWorld world) {
        this.server = server;
        this.world = world;
        this.entities = new Long2ObjectOpenHashMap<>();
        this.trackedEntityLinks = new Object2ObjectOpenHashMap<>();
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
        if (tracked != null) {
            tracked.sendPacket(new Packet0ERemoveEntity(entity.getEntityID()));
            tracked.getTrackingPlayers().forEach(player -> {
                Set<TrackedEntity> set = this.trackedEntityLinks.get(player);
                if (set != null) {
                    set.remove(tracked);
                }
            });
        }
        if (entity instanceof LPlayer) {
            LPlayer casted = (LPlayer) entity;
            Set<TrackedEntity> trackedEntities = this.trackedEntityLinks.remove(casted);
            if (trackedEntities != null) {
                trackedEntities.forEach(trackedEntity -> trackedEntity.updatePlayer(casted));
            }
        }
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
        TrackedEntity tracked = new TrackedEntity(this, entity, updateFrequency, viewDistance);
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
            entity.update();
        }
    }
}
