package org.lunaris.world.tracker;

import org.lunaris.LunarisServer;
import org.lunaris.entity.LEntity;
import org.lunaris.entity.LPlayer;
import org.lunaris.inventory.LPlayerInventory;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.material.Material;
import org.lunaris.network_old.protocol.MinePacket;
import org.lunaris.network.packet.Packet0ERemoveEntity;
import org.lunaris.network.packet.Packet12MoveEntity;
import org.lunaris.network.packet.Packet20MobArmorEquipment;
import org.lunaris.network.packet.Packet27SetEntityData;
import org.lunaris.network.packet.Packet28SetEntityMotion;
import org.lunaris.util.math.MathHelper;
import org.lunaris.api.world.Location;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author xtrafrancyz
 */
public class TrackedEntity {
    private final LEntity entity;
    private final int updatePeriod;
    private final int viewDistanceSquared;
    private final Set<LPlayer> trackingPlayers;
    private Location sentLocation;
    private int tickCounter = 0;

    public TrackedEntity(LEntity entity, int updatePeriod, int viewDistance) {
        this.entity = entity;
        this.updatePeriod = updatePeriod;
        this.viewDistanceSquared = viewDistance * viewDistance;
        this.trackingPlayers = new HashSet<>();
        this.sentLocation = entity.getLocation().add(0, -99, 0);
    }

    /**
     * Отпраляет пакет всем игрокам, смотрящим за этим ентити
     */
    public void sendPacket(MinePacket packet) {
        LunarisServer.getInstance().getNetworkManager().sendPacket(trackingPlayers, packet);
    }

    public void update(Collection<LPlayer> players) {
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
                //System.out.println(entity + " to " + player);
                trackingPlayers.add(player);
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
        } else {
            trackingPlayers.remove(player);
            player.sendPacket(new Packet0ERemoveEntity(entity.getEntityID()));
        }
    }

    public boolean isInViewRange(LPlayer player) {
        return MathHelper.pow2(entity.getX() - player.getX()) + MathHelper.pow2(entity.getZ() - player.getZ()) < viewDistanceSquared;
    }

    public Set<LPlayer> getTrackingPlayers() {
        return new HashSet<>(trackingPlayers);
    }
}
