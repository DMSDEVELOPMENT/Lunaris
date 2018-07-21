package org.lunaris.world.tracker;

import org.lunaris.LunarisServer;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.material.Material;
import org.lunaris.api.world.Location;
import org.lunaris.entity.LEntity;
import org.lunaris.entity.LPlayer;
import org.lunaris.inventory.LPlayerInventory;
import org.lunaris.network.Packet;
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
        LunarisServer.getInstance().getNetworkManager().sendPacket(this.trackingPlayers, packet);
    }

    public void update() {
        sendMetadata();
        if (this.entity.hasJustMoved()) {
            sendPacket(new Packet12MoveEntity(this.entity));
            sendPacket(new Packet28SetEntityMotion(this.entity));
        }
        this.tickCounter++;
    }

    private void sendMetadata() {
        if (this.entity.getMetadata().isDirtyMetadata()) {
            this.entity.getMetadata().setDirtyMetadata(false);
            sendPacket(new Packet27SetEntityData(this.entity.getEntityID(), this.entity.getMetadata().getDataProperties()));
            if (this.entity instanceof LPlayer) {
                ((LPlayer) this.entity).sendPacket(new Packet27SetEntityData(this.entity.getEntityID(), this.entity.getMetadata().getDataProperties()));
            }
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
            if (!this.trackingPlayers.contains(player) && this.entity.getWorld().isInRangeOfView(player, this.entity.getLocation().getChunk())) {
                this.trackingPlayers.add(player);
                this.tracker.trackedEntityLinks.computeIfAbsent(player, p -> new HashSet<>()).add(this);
                player.sendPacket(entity.createSpawnPacket());
                if (this.entity.getMotionX() != 0 || this.entity.getMotionY() != 0 || this.entity.getMotionZ() != 0) {
                    player.sendPacket(new Packet28SetEntityMotion(this.entity));
                }
                if (this.entity instanceof LPlayer) {
                    LPlayerInventory inv = ((LPlayer) this.entity).getInventory();
                    boolean hasArmor = false;
                    ItemStack[] armor = new ItemStack[4];
                    for (int i = 0; i < 4; i++) {
                        armor[i] = inv.getItem(inv.getSize() + i);
                        if (armor[i].getType() != Material.AIR)
                            hasArmor = true;
                    }
                    if (hasArmor)
                        player.sendPacket(new Packet20MobArmorEquipment(this.entity.getEntityID(), armor));
                }

                // Potion effects
            }
        } else if (this.trackingPlayers.remove(player)) {
            this.tracker.trackedEntityLinks.get(player).remove(this);
            player.sendPacket(new Packet0ERemoveEntity(this.entity.getEntityID()));
        }
    }

    void removePlayer(LPlayer player) {
        this.trackingPlayers.remove(player);
    }

    public boolean isInViewRange(LPlayer player) {
        return MathHelper.pow2(this.entity.getX() - player.getX()) + MathHelper.pow2(this.entity.getZ() - player.getZ()) < this.viewDistanceSquared;
    }

    public Set<LPlayer> getTrackingPlayers() {
        return new HashSet<>(this.trackingPlayers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackedEntity that = (TrackedEntity) o;
        return Objects.equals(this.entity, that.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.entity);
    }

}
