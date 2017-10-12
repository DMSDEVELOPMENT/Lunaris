package org.lunaris.event.player;

import org.lunaris.entity.LPlayer;
import org.lunaris.event.Event;

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
