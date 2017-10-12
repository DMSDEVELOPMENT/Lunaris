package org.lunaris.api.event;

import org.lunaris.LunarisServer;

/**
 * Created by RINES on 13.09.17.
 */
public abstract class Event {

    public void call() {
        LunarisServer.getInstance().getEventManager().call(this);
    }

}
