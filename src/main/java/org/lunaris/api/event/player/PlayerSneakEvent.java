package org.lunaris.api.event.player;

import org.lunaris.entity.LPlayer;
import org.lunaris.api.event.Event;

/**
 * Created by RINES on 16.09.17.
 */
public class PlayerSneakEvent extends Event {

    private final LPlayer player;
    private final State state;

    public PlayerSneakEvent(LPlayer player, State state) {
        this.player = player;
        this.state = state;
    }

    public LPlayer getPlayer() {
        return player;
    }

    public State getState() {
        return state;
    }

    public enum State {
        START_SNEAKING, STOP_SNEAKING
    }

}
