package org.lunaris.api.event.player;

import org.lunaris.api.event.Event;
import org.lunaris.api.world.Location;
import org.lunaris.entity.LPlayer;

/**
 * Created by RINES on 30.09.17.
 */
public class PlayerRespawnEvent extends Event {

    private final LPlayer player;
    private Location location;

    public PlayerRespawnEvent(LPlayer player, Location location) {
        this.player = player;
        this.location = location;
    }

    public LPlayer getPlayer() {
        return this.player;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
