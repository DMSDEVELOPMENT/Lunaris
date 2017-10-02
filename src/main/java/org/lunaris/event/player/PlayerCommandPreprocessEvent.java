package org.lunaris.event.player;

import org.lunaris.entity.Player;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;

/**
 * @author xtrafrancyz
 */
public class PlayerCommandPreprocessEvent extends Event implements Cancellable {
    private Player player;
    private String command;
    private boolean cancelled;

    public PlayerCommandPreprocessEvent(Player player, String command) {
        this.player = player;
        this.command = command;
    }

    public Player getPlayer() {
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
