package org.lunaris.event.player;

import org.lunaris.entity.LPlayer;
import org.lunaris.event.Event;

/**
 * Created by RINES on 16.09.17.
 */
public class PlayerJumpEvent extends Event {

    private final LPlayer player;

    public PlayerJumpEvent(LPlayer player) {
        this.player = player;
    }

    public LPlayer getPlayer() {
        return player;
    }

}
