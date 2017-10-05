package org.lunaris.event.player;

import org.lunaris.entity.Entity;
import org.lunaris.entity.Player;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;
import org.lunaris.item.ItemStack;
import org.lunaris.material.Material;

/**
 * Created by RINES on 05.10.17.
 */
public class PlayerInteractEntityEvent extends Event implements Cancellable {

    private final Player player;
    private final Entity entity;
    private boolean cancelled;

    public PlayerInteractEntityEvent(Player player, Entity entity) {
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

    public Player getPlayer() {
        return this.player;
    }

    public Entity getEntity() {
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
