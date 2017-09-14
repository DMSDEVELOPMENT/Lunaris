package org.lunaris.event.player;

import org.lunaris.entity.Player;
import org.lunaris.event.Event;

/**
 * Created by RINES on 13.09.17.
 */
public class PlayerDisconnectEvent extends Event {

    private final Player player;

    public PlayerDisconnectEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

}
