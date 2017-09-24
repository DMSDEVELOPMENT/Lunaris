package org.lunaris.event.entity;

import org.lunaris.entity.LivingEntity;
import org.lunaris.event.Event;

/**
 * Created by RINES on 25.09.17.
 */
public class EntityDeathEvent extends Event {

    private final LivingEntity entity;

    public EntityDeathEvent(LivingEntity entity) {
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }

}
