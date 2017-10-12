package org.lunaris.api.event;

import org.lunaris.LunarisServer;

/**
 * Created by RINES on 13.09.17.
 */
public interface Listener {

    default void register() {
        LunarisServer.getInstance().getEventManager().register(this);
    }

}
