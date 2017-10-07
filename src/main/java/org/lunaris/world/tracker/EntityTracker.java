package org.lunaris.world.tracker;

import org.lunaris.Lunaris;
import org.lunaris.entity.Entity;
import org.lunaris.entity.Player;
import org.lunaris.network.protocol.MinePacket;
import org.lunaris.network.protocol.packet.Packet0ERemoveEntity;
import org.lunaris.world.World;
import org.lunaris.world.util.LongObjectHashMap;

import java.util.Set;

/**
 * @author xtrafrancyz
 */
public class EntityTracker {
    private final Lunaris server;
    private final World world;
    private LongObjectHashMap<TrackedEntity> entities;

    public EntityTracker(Lunaris server, World world) {
        this.server = server;
        this.world = world;
        this.entities = new LongObjectHashMap<>();
    }

    public void track(Entity entity) {
        if (entity instanceof Player) {
            registerEntity(entity, entity.getTrackRange(), 2);
        } else {
            registerEntity(entity, entity.getTrackRange(), 3);
        }
    }

    public void untrack(Entity entity) {
        TrackedEntity tracked = entities.remove(entity.getEntityID());
        if (tracked != null)
            tracked.sendPacket(new Packet0ERemoveEntity(entity.getEntityID()));
    }

    private void registerEntity(Entity entity, int viewDistance, int updateFrequency) {
        if (entities.containsKey(entity.getEntityID())) {
            server.getLogger().warn("Duplicate entity in tracker " + entity);
            return;
        }
        if (entity instanceof Player) {
            for (TrackedEntity tracked : entities.values())
                tracked.updatePlayer((Player) entity);
        }
        TrackedEntity tracked = new TrackedEntity(entity, updateFrequency, viewDistance);
        entities.put(entity.getEntityID(), tracked);
        tracked.updatePlayers(world.getPlayers());
    }

    public void sendPacketToWatchers(Entity entity, MinePacket packet) {
        entities.get(entity.getEntityID()).sendPacket(packet);
    }

    public Set<Player> getWatchers(Entity entity) {
        return entities.get(entity.getEntityID()).getTrackingPlayers();
    }

    public void tick() {
        for (TrackedEntity entity : entities.values()) {
            entity.update(world.getPlayers());
        }
    }
}
