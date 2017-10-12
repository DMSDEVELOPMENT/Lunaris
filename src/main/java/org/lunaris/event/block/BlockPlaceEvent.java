package org.lunaris.event.block;

import org.lunaris.api.entity.Player;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;
import org.lunaris.api.item.ItemStack;
import org.lunaris.world.BlockVector;

/**
 * Created by RINES on 02.10.17.
 */
public class BlockPlaceEvent extends Event implements Cancellable {

    private final Player player;
    private final ItemStack itemInHand;
    private final BlockVector placeLocation;
    private boolean cancelled;

    public BlockPlaceEvent(Player player, ItemStack itemInHand, BlockVector placeLocation) {
        this.player = player;
        this.itemInHand = itemInHand;
        this.placeLocation = placeLocation;
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

    public ItemStack getItemInHand() {
        return this.itemInHand;
    }

    public BlockVector getPlaceLocation() {
        return this.placeLocation;
    }

}
