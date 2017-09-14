package org.lunaris.server;

/**
 * Created by RINES on 13.09.17.
 */
public class EntityProvider {

    private volatile int entityIDincrementor;

    public int getNextEntityID() {
        return ++this.entityIDincrementor;
    }

}
