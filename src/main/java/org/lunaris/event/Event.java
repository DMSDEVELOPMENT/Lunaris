package org.lunaris.event;

import org.lunaris.Lunaris;

/**
 * Created by RINES on 13.09.17.
 */
public abstract class Event {

    public void call() {
        Lunaris.getInstance().getEventManager().call(this);
    }

}
