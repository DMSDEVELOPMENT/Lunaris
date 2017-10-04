package org.lunaris.event.entity;

import org.lunaris.entity.LivingEntity;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;

/**
 * Created by RINES on 24.09.17.
 */
public class EntityDamageEvent extends Event implements Cancellable {

    private final LivingEntity entity;
    private final DamageCause damageCause;
    private double damage;
    private boolean cancelled;

    public EntityDamageEvent(LivingEntity entity, DamageCause damageCause, double damage) {
        this.entity = entity;
        this.damageCause = damageCause;
        this.damage = damage;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public DamageCause getDamageCause() {
        return damageCause;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public enum DamageCause {
        UNKNOWN,
        FIRE,
        ENTITY_ATTACK,
        VOID,
        FALL
    }

}
