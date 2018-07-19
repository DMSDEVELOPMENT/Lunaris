package org.lunaris.api.event.player;

import org.lunaris.api.event.Event;
import org.lunaris.entity.LPlayer;

/**
 * Created by RINES on 14.09.17.
 */
public class PlayerJoinEvent extends Event {

    private final LPlayer player;

    public PlayerJoinEvent(LPlayer player) {
        this.player = player;
    }

    public LPlayer getPlayer() {
        return player;
    }

}
