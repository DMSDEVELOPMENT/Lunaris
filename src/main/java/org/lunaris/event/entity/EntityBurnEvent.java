package org.lunaris.event.entity;

import org.lunaris.entity.Entity;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;

/**
 * Created by RINES on 24.09.17.
 */
public class EntityBurnEvent extends Event implements Cancellable {

    private final Entity entity;
    private int fireTicks;
    private boolean cancelled;

    public EntityBurnEvent(Entity entity, int fireTicks) {
        this.entity = entity;
        this.fireTicks = fireTicks;
    }

    public Entity getEntity() {
        return entity;
    }

    public int getFireTicks() {
        return fireTicks;
    }

    public void setFireTicks(int fireTicks) {
        this.fireTicks = fireTicks;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
    
}
