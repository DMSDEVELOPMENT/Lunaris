package org.lunaris.event.player;

import org.lunaris.block.LBlock;
import org.lunaris.entity.LPlayer;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;

/**
 * Created by RINES on 24.09.17.
 */
public class PlayerInteractEvent extends Event implements Cancellable {

    private final LPlayer player;
    private final Action action;
    private final LBlock clickedBlock;

    private boolean cancelled;

    public PlayerInteractEvent(LPlayer player, Action action, LBlock clickedBlock) {
        this.player = player;
        this.action = action;
        this.clickedBlock = clickedBlock;
    }

    public LPlayer getPlayer() {
        return player;
    }

    public Action getAction() {
        return action;
    }

    public LBlock getClickedBlock() {
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
