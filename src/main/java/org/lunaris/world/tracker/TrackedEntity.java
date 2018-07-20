package org.lunaris.world.tracker;

import org.lunaris.LunarisServer;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.material.Material;
import org.lunaris.api.world.Location;
import org.lunaris.entity.LEntity;
import org.lunaris.entity.LPlayer;
import org.lunaris.inventory.LPlayerInventory;
import org.lunaris.network.Packet;
import org.lunaris.network.PlayerConnectionState;
import org.lunaris.network.packet.*;
import org.lunaris.util.math.MathHelper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author xtrafrancyz
 */
public class TrackedEntity {

    private final EntityTracker tracker;
    private final LEntity entity;
    private final int updatePeriod;
    private final int viewDistanceSquared;
    private final Set<LPlayer> trackingPlayers;
    private Location sentLocation;
    private int tickCounter = 0;

    TrackedEntity(EntityTracker tracker, LEntity entity, int updatePeriod, int viewDistance) {
        this.tracker = tracker;
        this.entity = entity;
        this.updatePeriod = updatePeriod;
        this.viewDistanceSquared = viewDistance * viewDistance;
        this.trackingPlayers = new HashSet<>();
        this.sentLocation = entity.getLocation().add(0, -99, 0);
    }

    /**
     * Отпраляет пакет всем игрокам, смотрящим за этим ентити
     */
    public void sendPacket(Packet packet) {
        LunarisServer.getInstance().getNetworkManager().sendPacket(trackingPlayers, packet);
    }

    public void update() {
        sendMetadata();
        if (entity.hasJustMoved()) {
            sendPacket(new Packet12MoveEntity(entity));
            sendPacket(new Packet28SetEntityMotion(entity));
        }
        tickCounter++;
    }

    private void sendMetadata() {
        if (entity.isDirtyMetadata()) {
            entity.setDirtyMetadata(false);
            sendPacket(new Packet27SetEntityData(this.entity.getEntityID(), entity.getDataProperties()));
            if (entity instanceof LPlayer)
                ((LPlayer) entity).sendPacket(new Packet27SetEntityData(this.entity.getEntityID(), entity.getDataProperties()));
        }
    }

    public void updatePlayers(Collection<LPlayer> players) {
        for (LPlayer player : players) {
            updatePlayer(player);
        }
    }

    public void updatePlayer(LPlayer player) {
        if (player == entity)
            return;
        if (isInViewRange(player)) {
            if (!trackingPlayers.contains(player) && entity.getWorld().isInRangeOfView(player, entity.getLocation().getChunk())) {
                this.trackingPlayers.add(player);
                this.tracker.trackedEntityLinks.computeIfAbsent(player, p -> new HashSet<>()).add(this);
                player.sendPacket(entity.createSpawnPacket());
                if (entity.getMotionX() != 0 || entity.getMotionY() != 0 || entity.getMotionZ() != 0) {
                    player.sendPacket(new Packet28SetEntityMotion(entity));
                }
                if (entity instanceof LPlayer) {
                    LPlayerInventory inv = ((LPlayer) entity).getInventory();
                    boolean hasArmor = false;
                    ItemStack[] armor = new ItemStack[4];
                    for (int i = 0; i < 4; i++) {
                        armor[i] = inv.getItem(inv.getSize() + i);
                        if (armor[i].getType() != Material.AIR)
                            hasArmor = true;
                    }
                    if (hasArmor)
                        player.sendPacket(new Packet20MobArmorEquipment(entity.getEntityID(), armor));
                }

                // Potion effects
            }
        } else if (this.trackingPlayers.remove(player)) {
            this.tracker.trackedEntityLinks.get(player).remove(this);
            player.sendPacket(new Packet0ERemoveEntity(entity.getEntityID()));
        }
    }

    public void removePlayer(LPlayer player) {
        if (this.trackingPlayers.remove(player)) {
            this.tracker.trackedEntityLinks.get(player).remove(this);
        }
    }

    public boolean isInViewRange(LPlayer player) {
        return MathHelper.pow2(entity.getX() - player.getX()) + MathHelper.pow2(entity.getZ() - player.getZ()) < viewDistanceSquared;
    }

    public Set<LPlayer> getTrackingPlayers() {
        return new HashSet<>(trackingPlayers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackedEntity that = (TrackedEntity) o;
        return Objects.equals(entity, that.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entity);
    }

}
