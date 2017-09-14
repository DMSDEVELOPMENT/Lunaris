package org.lunaris.event.player;

import org.lunaris.entity.Player;
import org.lunaris.event.Event;

/**
 * Created by RINES on 14.09.17.
 */
public class PlayerJoinEvent extends Event {

    private final Player player;

    public PlayerJoinEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

}
