package org.lunaris.event.player;

import org.lunaris.block.Block;
import org.lunaris.entity.Player;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;

/**
 * Created by RINES on 24.09.17.
 */
public class PlayerInteractEvent extends Event implements Cancellable {

    private final Player player;
    private final Action action;
    private final Block clickedBlock;

    private boolean cancelled;

    public PlayerInteractEvent(Player player, Action action, Block clickedBlock) {
        this.player = player;
        this.action = action;
        this.clickedBlock = clickedBlock;
    }

    public Player getPlayer() {
        return player;
    }

    public Action getAction() {
        return action;
    }

    public Block getClickedBlock() {
        return clickedBlock;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public enum Action {
        LEFT_CLICK_AIR,
        LEFT_CLICK_BLOCK,
        RIGHT_CLICK_AIR,
        RIGHT_CLICK_BLOCK
    }

}
