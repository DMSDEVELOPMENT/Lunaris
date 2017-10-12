package org.lunaris.event.player;

import org.lunaris.entity.LEntity;
import org.lunaris.entity.LPlayer;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.material.Material;

/**
 * Created by RINES on 05.10.17.
 */
public class PlayerInteractEntityEvent extends Event implements Cancellable {

    private final LPlayer player;
    private final LEntity entity;
    private boolean cancelled;

    public PlayerInteractEntityEvent(LPlayer player, LEntity entity) {
        this.player = player;
        this.entity = entity;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public LPlayer getPlayer() {
        return this.player;
    }

    public LEntity getEntity() {
        return this.entity;
    }

    public ItemStack getItem() {
        return this.player.getInventory().getItemInHand();
    }

    public boolean hasItem() {
        ItemStack item = getItem();
        return item != null && item.getType() != Material.AIR;
    }

}
