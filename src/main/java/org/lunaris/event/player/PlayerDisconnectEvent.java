package org.lunaris.event.player;

import org.lunaris.entity.LPlayer;
import org.lunaris.event.Event;

/**
 * Created by RINES on 13.09.17.
 */
public class PlayerDisconnectEvent extends Event {

    private final LPlayer player;

    public PlayerDisconnectEvent(LPlayer player) {
        this.player = player;
    }

    public LPlayer getPlayer() {
        return player;
    }

}
