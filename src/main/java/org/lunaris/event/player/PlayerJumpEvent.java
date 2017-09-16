package org.lunaris.event.player;

import org.lunaris.entity.Player;
import org.lunaris.event.Event;

/**
 * Created by RINES on 16.09.17.
 */
public class PlayerJumpEvent extends Event {

    private final Player player;

    public PlayerJumpEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

}
