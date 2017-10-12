package org.lunaris.api.event.entity;

import org.lunaris.api.entity.LivingEntity;
import org.lunaris.entity.damage.DamageCalculus;
import org.lunaris.api.entity.damage.DamageSource;
import org.lunaris.api.event.Cancellable;
import org.lunaris.api.event.Event;

/**
 * Created by RINES on 24.09.17.
 */
public class EntityDamageEvent extends Event implements Cancellable {

    private final LivingEntity entity;
    private final DamageSource damageSource;
    private double damage;
    private double finalDamage;
    private boolean cancelled;

    public EntityDamageEvent(LivingEntity entity, DamageSource damageSource, double damage) {
        this.entity = entity;
        this.damageSource = damageSource;
        setDamage(damage);
    }

    public LivingEntity getEntity() {
        return this.entity;
    }

    public DamageSource getDamageSource() {
        return this.damageSource;
    }

    public double getDamage() {
        return this.damage;
    }

    public double getFinalDamage() {
        return this.finalDamage == -1D ? this.finalDamage = DamageCalculus.calculateIncomingDamage(this.entity, this.damageSource, this.damage) : this.finalDamage;
    }

    public void setFinalDamage(double damage) {
        this.finalDamage = damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
        this.finalDamage = -1D;
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
