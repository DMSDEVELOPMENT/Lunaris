package org.lunaris.event.player;

import org.lunaris.entity.Player;
import org.lunaris.event.Event;
import org.lunaris.world.Location;

/**
 * Created by RINES on 30.09.17.
 */
public class PlayerRespawnEvent extends Event {

    private final Player player;
    private Location location;

    public PlayerRespawnEvent(Player player, Location location) {
        this.player = player;
        this.location = location;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
