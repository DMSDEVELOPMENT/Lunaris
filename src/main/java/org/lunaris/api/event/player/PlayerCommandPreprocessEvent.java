package org.lunaris.api.event.player;

import org.lunaris.entity.LPlayer;
import org.lunaris.api.event.Cancellable;
import org.lunaris.api.event.Event;

/**
 * @author xtrafrancyz
 */
public class PlayerCommandPreprocessEvent extends Event implements Cancellable {
    private LPlayer player;
    private String command;
    private boolean cancelled;

    public PlayerCommandPreprocessEvent(LPlayer player, String command) {
        this.player = player;
        this.command = command;
    }

    public LPlayer getPlayer() {
        return this.player;
    }

    public String getCommand() {
        return this.command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
}
