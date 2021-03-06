package org.lunaris.api.event.player;

import org.lunaris.api.event.Event;
import org.lunaris.entity.LPlayer;

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
