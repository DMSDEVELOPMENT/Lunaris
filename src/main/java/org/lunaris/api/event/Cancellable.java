package org.lunaris.api.event;

/**
 * Created by RINES on 13.09.17.
 */
public interface Cancellable {

    void setCancelled(boolean value);

    boolean isCancelled();

}
