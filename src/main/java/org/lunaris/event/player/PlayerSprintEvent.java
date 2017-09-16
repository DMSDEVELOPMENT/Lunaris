package org.lunaris.event.player;

import org.lunaris.entity.Player;
import org.lunaris.event.Event;

/**
 * Created by RINES on 16.09.17.
 */
public class PlayerSprintEvent extends Event {

    private final Player player;
    private final State state;

    public PlayerSprintEvent(Player player, State state) {
        this.player = player;
        this.state = state;
    }

    public Player getPlayer() {
        return player;
    }

    public State getState() {
        return state;
    }

    public enum State {
        START_SPRINTING, STOP_SPRINTING
    }

}
